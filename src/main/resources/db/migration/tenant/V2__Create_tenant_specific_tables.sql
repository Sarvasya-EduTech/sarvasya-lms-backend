-- V2__Create_tenant_specific_tables.sql
-- This migration will be applied to each tenant schema
-- Note: This is a template that will be executed for each tenant

-- Create tenant-specific users table (if not exists)
CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    requires_password_change BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP
);

-- Create theme_settings table
CREATE TABLE IF NOT EXISTS theme_settings (
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
    widget_input_border_color VARCHAR(255),
    logo_url VARCHAR(255),
    logo_version BIGINT
);

-- Create buses table
CREATE TABLE IF NOT EXISTS buses (
    id UUID NOT NULL PRIMARY KEY,
    bus_number VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    created_at TIMESTAMP
);

-- Create bus_schedules table
CREATE TABLE IF NOT EXISTS bus_schedules (
    id UUID NOT NULL PRIMARY KEY,
    bus_id UUID NOT NULL,
    route_name VARCHAR(255) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP,
    CONSTRAINT fk_bus_schedule_bus FOREIGN KEY (bus_id) REFERENCES buses(id)
);

-- Create bus_passes table
CREATE TABLE IF NOT EXISTS bus_passes (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    bus_id UUID NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    CONSTRAINT fk_bus_pass_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bus_pass_bus FOREIGN KEY (bus_id) REFERENCES buses(id)
);