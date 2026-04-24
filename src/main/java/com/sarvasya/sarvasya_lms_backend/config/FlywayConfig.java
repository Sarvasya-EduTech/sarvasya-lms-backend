package com.sarvasya.sarvasya_lms_backend.config;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import com.sarvasya.sarvasya_lms_backend.config.db.ShardDataSourceManager;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);
    private static final String GLOBAL_SCHEMA = "tenant";

    @Value("${spring.flyway.baseline-on-migrate:true}")
    private boolean baselineOnMigrate;

    private final Set<String> migratedSchemas = ConcurrentHashMap.newKeySet();

    @Bean
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
    public Flyway flyway(DataSource dataSource, ObjectProvider<ShardDataSourceManager> shardDataSourceManagerProvider) {
        // Configure Flyway for the global/shared schema ("tenant").
        // Tenant schemas are migrated separately via migrateTenantSchema().
        ensureSchemaExists(dataSource, GLOBAL_SCHEMA);

        Flyway flyway = migrateGlobalSchema(dataSource);

        // Sharded mode: each shard is a different physical database, so the global schema must exist on every shard.
        ShardDataSourceManager shardManager = shardDataSourceManagerProvider.getIfAvailable();
        if (shardManager != null && shardManager.isEnabled()) {
            for (String shardId : shardManager.getShardIds()) {
                DataSource shardWriter = shardManager.writerForShardId(shardId);
                if (shardWriter == null) {
                    continue;
                }
                try {
                    migrateGlobalSchema(shardWriter);
                } catch (Exception e) {
                    throw new RuntimeException("Flyway global schema migration failed for shard " + shardId, e);
                }
            }
        }

        return flyway;
    }

    /**
     * Migrates a specific tenant schema using Flyway.
     * This should be called when initializing a new tenant schema.
     * Tenant schemas only get tenant-specific tables, not the central schema.
     */
    public void migrateTenantSchema(DataSource dataSource, String tenantIdentifier) {
        // Never run tenant-scoped migrations on global/shared schemas.
        if (tenantIdentifier == null) {
            return;
        }
        String normalized = tenantIdentifier.trim().toLowerCase();
        if (normalized.isEmpty() || normalized.equals(GLOBAL_SCHEMA) || normalized.equals("public")) {
            log.debug("Skipping tenant migration for global schema [{}]", tenantIdentifier);
            return;
        }

        if (migratedSchemas.contains(normalized)) {
            return;
        }

        log.info("Starting Flyway migration for tenant schema [{}]", normalized);

        try {
            // Step 1: Ensure the schema exists before Flyway runs
            ensureSchemaExists(dataSource, normalized);

            // Step 2: Apply tenant-specific migrations to the tenant schema
            log.debug("Running Flyway migrations on schema [{}]", normalized);
            Flyway tenantFlyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
                    .schemas(normalized)
                    .table("flyway_schema_history") // Separate history table per schema
                    .placeholders(Map.of("schema", normalized))
                    .outOfOrder(false)
                    .validateOnMigrate(true)
                    .load();

            tenantFlyway.migrate();

            migratedSchemas.add(normalized);
            log.info("Successfully migrated tenant schema [{}]", normalized);

        } catch (Exception e) {
            log.error("Critical error migrating tenant schema [{}]", normalized, e);
            throw new RuntimeException("Could not migrate tenant schema", e);
        }
    }

    private static void ensureSchemaExists(DataSource dataSource, String schema) {
        if (schema == null || schema.isBlank()) return;
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + schema + "\"");
        } catch (Exception e) {
            throw new RuntimeException("Could not ensure schema exists: " + schema, e);
        }
    }

    private Flyway migrateGlobalSchema(DataSource dataSource) {
        ensureSchemaExists(dataSource, GLOBAL_SCHEMA);
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/schema")
                .baselineOnMigrate(baselineOnMigrate)
                .schemas(GLOBAL_SCHEMA)
                .load();
        flyway.migrate();
        return flyway;
    }

    public Set<String> getMigratedSchemas() {
        return migratedSchemas;
    }
}








