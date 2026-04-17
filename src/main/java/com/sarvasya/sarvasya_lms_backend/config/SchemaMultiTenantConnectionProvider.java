package com.sarvasya.sarvasya_lms_backend.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
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

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
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
            initializeSchemaAndTables(connection, tenantIdentifier);
        }

        connection.setSchema(tenantIdentifier);
        return connection;
    }

    private synchronized void initializeSchemaAndTables(Connection connection, String tenantIdentifier) throws SQLException {
        // Double check in case another thread initialized it while we were waiting
        if (initializedSchemas.contains(tenantIdentifier)) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            // 1. Create the schema dynamically
            statement.execute("CREATE SCHEMA IF NOT EXISTS \"" + tenantIdentifier + "\"");
            
            // 2. Create the required tables in this new schema
            // (In a full production scenario, you would trigger Flyway/Liquibase here)
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS "%s".users (
                    id uuid NOT NULL PRIMARY KEY,
                    email varchar(255) NOT NULL UNIQUE,
                    name varchar(255) NOT NULL,
                    password varchar(255) NOT NULL,
                    role varchar(255) NOT NULL
                )
            """.formatted(tenantIdentifier);
            
            statement.execute(createUsersTable);
            
            initializedSchemas.add(tenantIdentifier);
        }
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        connection.setSchema("public");
        connection.close();
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
}
