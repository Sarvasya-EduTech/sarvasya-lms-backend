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

            String createThemeSettingsTable = """
                CREATE TABLE IF NOT EXISTS "%s".theme_settings (
                    id SERIAL PRIMARY KEY,
                    primary_seed_color VARCHAR(255),
                    primary_gradient_start VARCHAR(255),
                    primary_gradient_end VARCHAR(255),
                    primary_gradient_dir INTEGER,
                    primary_use_gradient BOOLEAN,
                    primary_text_color VARCHAR(255),
                    secondary_background_color VARCHAR(255),
                    secondary_gradient_start VARCHAR(255),
                    secondary_gradient_end VARCHAR(255),
                    secondary_gradient_dir INTEGER,
                    secondary_use_gradient BOOLEAN,
                    secondary_text_color VARCHAR(255),
                    sidebar_seed_color VARCHAR(255),
                    sidebar_gradient_start VARCHAR(255),
                    sidebar_gradient_end VARCHAR(255),
                    sidebar_gradient_dir INTEGER,
                    sidebar_use_gradient BOOLEAN,
                    sidebar_text_color VARCHAR(255),
                    widget_card_background_color VARCHAR(255),
                    widget_card_elevation DOUBLE PRECISION,
                    widget_button_background_color VARCHAR(255),
                    widget_button_text_color VARCHAR(255),
                    widget_input_background_color VARCHAR(255),
                    widget_input_border_color VARCHAR(255)
                )
            """.formatted(tenantIdentifier);
            
            statement.execute(createThemeSettingsTable);
            
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
