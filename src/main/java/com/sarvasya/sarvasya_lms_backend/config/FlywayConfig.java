package com.sarvasya.sarvasya_lms_backend.config;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);

    @Value("${spring.flyway.baseline-on-migrate:true}")
    private boolean baselineOnMigrate;

    private final Set<String> migratedSchemas = ConcurrentHashMap.newKeySet();

    @Bean
    @ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
    public Flyway flyway(DataSource dataSource) {
        // Configure Flyway for the default schema (public)
        // Only run schema migrations, NOT tenant-specific migrations
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/schema") // Only schema migrations for public schema
                .baselineOnMigrate(baselineOnMigrate)
                .schemas("public") // Default schema
                .load();

        // Migrate the default schema
        flyway.migrate();

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
        if (normalized.isEmpty() || normalized.equals("tenant") || normalized.equals("public")) {
            log.debug("Skipping tenant migration for global schema [{}]", tenantIdentifier);
            return;
        }

        if (migratedSchemas.contains(normalized)) {
            return;
        }

        log.info("Starting Flyway migration for tenant schema [{}]", normalized);

        try {
            // Step 1: Ensure the schema exists before Flyway runs
            log.debug("Creating schema if not exists [{}]", normalized);
            try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + normalized + "\"");
            }

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

            // Repair checksums first (handles cases where migration files were updated)
            tenantFlyway.repair();
            tenantFlyway.migrate();

            migratedSchemas.add(normalized);
            log.info("Successfully migrated tenant schema [{}]", normalized);

        } catch (Exception e) {
            log.error("Critical error migrating tenant schema [{}]", normalized, e);
            throw new RuntimeException("Could not migrate tenant schema", e);
        }
    }

    public Set<String> getMigratedSchemas() {
        return migratedSchemas;
    }
}








