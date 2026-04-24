package com.sarvasya.sarvasya_lms_backend.config;

import com.sarvasya.sarvasya_lms_backend.config.db.ShardDataSourceManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import javax.sql.DataSource;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private static final Logger log = LoggerFactory.getLogger(SchemaMultiTenantConnectionProvider.class);

    /**
     * Fallback datasource (single DB mode). Also used for "any connection".
     */
    private final DataSource dataSource;
    private final ShardDataSourceManager shardDataSourceManager;
    private final Set<String> initializedSchemas = ConcurrentHashMap.newKeySet();
    private final Set<String> migrationFailedSchemas = ConcurrentHashMap.newKeySet();

    @Value("${app.tenancy.auto-migrate-on-request:false}")
    private boolean autoMigrateOnRequest;

    private final FlywayConfig flywayConfig;

    public SchemaMultiTenantConnectionProvider(
            DataSource dataSource,
            ObjectProvider<ShardDataSourceManager> shardDataSourceManagerProvider,
            ObjectProvider<FlywayConfig> flywayConfigProvider) {
        this.dataSource = dataSource;
        this.shardDataSourceManager = shardDataSourceManagerProvider.getIfAvailable();
        this.flywayConfig = flywayConfigProvider.getIfAvailable();
        // Don't pre-add 'tenant' here, so it gets initialized on first use
        initializedSchemas.add("public");
        initializedSchemas.add("tenant");
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        if (tenantIdentifier == null || tenantIdentifier.isBlank()) {
            tenantIdentifier = "public";
        }
        tenantIdentifier = normalizeTenantIdentifier(tenantIdentifier);

        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        DataSource selected = selectDataSource(tenantIdentifier, readOnly);
        Connection connection = selected.getConnection();

        if (!initializedSchemas.contains(tenantIdentifier)) {
            initializeSchemaAndTables(tenantIdentifier);
        }

        // For PostgreSQL, setting the search_path is the most reliable way to switch
        // schemas
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO \"" + tenantIdentifier + "\"");
        }

        return connection;
    }

    private synchronized void initializeSchemaAndTables(String tenantIdentifier) {
        if (initializedSchemas.contains(tenantIdentifier)) {
            return;
        }

        log.info("Initializing schema for tenant [{}]", tenantIdentifier);

        if (flywayConfig != null) {
            // Use Flyway for migration
            try {
                if (autoMigrateOnRequest) {
                    flywayConfig.migrateTenantSchema(writerDataSourceForTenant(tenantIdentifier), tenantIdentifier);
                } else if (!schemaExists(tenantIdentifier)) {
                    throw new RuntimeException(
                            "Schema '" + tenantIdentifier + "' is missing and auto migration is disabled. "
                                    + "Provision tenant before serving traffic.");
                }
                initializedSchemas.add(tenantIdentifier);
                migrationFailedSchemas.remove(tenantIdentifier);
            } catch (Exception e) {
                migrationFailedSchemas.add(tenantIdentifier);
                log.error("Critical error migrating tenant schema [{}]", tenantIdentifier, e);
                throw new RuntimeException("Could not initialize tenant schema", e);
            }
        } else {
            // Fallback: Manual schema creation if Flyway is not available
            initializeSchemaManually(tenantIdentifier);
        }
    }

    private boolean schemaExists(String tenantIdentifier) {
        try (Connection connection = writerDataSourceForTenant(tenantIdentifier).getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(
                        "SELECT 1 FROM information_schema.schemata WHERE schema_name = '" + tenantIdentifier + "'")) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to verify schema existence for tenant: " + tenantIdentifier, e);
        }
    }

    private String normalizeTenantIdentifier(String tenantIdentifier) {
        String normalized = tenantIdentifier.trim().toLowerCase();
        if (!normalized.matches("[a-z0-9_\\-]+")) {
            throw new IllegalArgumentException("Invalid tenant identifier");
        }
        return normalized;
    }

    private void initializeSchemaManually(String tenantIdentifier) {
        try (Connection connection = writerDataSourceForTenant(tenantIdentifier).getConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                // Create the tenant-specific schema
                statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + tenantIdentifier + "\"");
                connection.commit();
                initializedSchemas.add(tenantIdentifier);
                log.info("Successfully initialized tenant schema [{}]", tenantIdentifier);
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            log.error("Critical error initializing schema for [{}]", tenantIdentifier, e);
            throw new RuntimeException("Could not initialize tenant schema", e);
        }
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO public");
        } catch (SQLException e) {
            System.err.println("!!! Error resetting search_path on connection release: " + e.getMessage());
        } finally {
            connection.close();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    public Set<String> getAllTenants() {
        return initializedSchemas;
    }

    private DataSource selectDataSource(String tenantIdentifier, boolean readOnly) {
        if (shardDataSourceManager != null && shardDataSourceManager.isEnabled()) {
            return readOnly ? shardDataSourceManager.readerForTenant(tenantIdentifier)
                    : shardDataSourceManager.writerForTenant(tenantIdentifier);
        }
        return dataSource;
    }

    private DataSource writerDataSourceForTenant(String tenantIdentifier) {
        if (shardDataSourceManager != null && shardDataSourceManager.isEnabled()) {
            return shardDataSourceManager.writerForTenant(tenantIdentifier);
        }
        return dataSource;
    }
}








