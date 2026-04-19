package com.sarvasya.sarvasya_lms_backend.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class FlywayConfig {

    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String[] flywayLocations;

    @Value("${spring.flyway.baseline-on-migrate:true}")
    private boolean baselineOnMigrate;

    private final Set<String> migratedSchemas = ConcurrentHashMap.newKeySet();

    @Bean
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
        if (migratedSchemas.contains(tenantIdentifier)) {
            return;
        }

        System.out.println(">>> STARTING FLYWAY MIGRATION for tenant schema: [" + tenantIdentifier + "]");

        try {
            // Step 1: Ensure the schema exists before Flyway runs
            System.out.println(">>> Creating schema if not exists: " + tenantIdentifier);
            try (Connection connection = dataSource.getConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + tenantIdentifier + "\"");
            }

            // Step 2: Apply tenant-specific migrations to the tenant schema
            System.out.println(">>> Running Flyway migrations on schema: " + tenantIdentifier);
            Flyway tenantFlyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
                    .schemas(tenantIdentifier)
                    .table("flyway_schema_history") // Separate history table per schema
                    .placeholders(Map.of("schema", tenantIdentifier))
                    .load();

            tenantFlyway.migrate();

            migratedSchemas.add(tenantIdentifier);
            System.out.println(">>> SUCCESSFULLY MIGRATED tenant schema: " + tenantIdentifier);

        } catch (Exception e) {
            System.err.println(
                    "!!! CRITICAL ERROR migrating tenant schema [" + tenantIdentifier + "]: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not migrate tenant schema", e);
        }
    }

    public Set<String> getMigratedSchemas() {
        return migratedSchemas;
    }
}