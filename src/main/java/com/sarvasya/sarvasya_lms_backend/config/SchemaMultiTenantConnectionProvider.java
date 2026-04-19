package com.sarvasya.sarvasya_lms_backend.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;
    private final Set<String> initializedSchemas = ConcurrentHashMap.newKeySet();

    @Autowired(required = false)
    private FlywayConfig flywayConfig;

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
        // Don't pre-add 'tenant' here, so it gets initialized on first use
        initializedSchemas.add("public");
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
        Connection connection = getAnyConnection();

        if (!initializedSchemas.contains(tenantIdentifier)) {
            initializeSchemaAndTables(tenantIdentifier);
        }

        // For PostgreSQL, setting the search_path is the most reliable way to switch
        // schemas
        try (Statement statement = connection.createStatement()) {
            System.out.println("SQL: SET search_path TO \"" + tenantIdentifier + "\"");
            statement.execute("SET search_path TO \"" + tenantIdentifier + "\"");
        }

        return connection;
    }

    private synchronized void initializeSchemaAndTables(String tenantIdentifier) {
        if (initializedSchemas.contains(tenantIdentifier)) {
            return;
        }

        System.out.println(">>> START INITIALIZING schema for tenant: [" + tenantIdentifier + "]");

        if (flywayConfig != null) {
            // Use Flyway for migration
            try {
                flywayConfig.migrateTenantSchema(dataSource, tenantIdentifier);
                initializedSchemas.add(tenantIdentifier);
            } catch (Exception e) {
                System.err.println(
                        "!!! CRITICAL ERROR migrating tenant schema [" + tenantIdentifier + "]: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Could not initialize tenant schema", e);
            }
        } else {
            // Fallback: Manual schema creation if Flyway is not available
            initializeSchemaManually(tenantIdentifier);
        }
    }

    private void initializeSchemaManually(String tenantIdentifier) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                // Create the tenant-specific schema
                System.out.println(">>> Executing: CREATE SCHEMA IF NOT EXISTS \"" + tenantIdentifier + "\"");
                statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + tenantIdentifier + "\"");
                connection.commit();
                initializedSchemas.add(tenantIdentifier);
                System.out.println(">>> SUCCESSFULLY INITIALIZED tenant: " + tenantIdentifier);
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println(
                    "!!! CRITICAL ERROR initializing schema for [" + tenantIdentifier + "]: " + e.getMessage());
            e.printStackTrace();
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
}
