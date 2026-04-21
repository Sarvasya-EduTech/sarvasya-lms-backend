-- V2__Add_Theme_Settings.sql
-- Add theme_settings table to the global 'tenant' schema

CREATE TABLE IF NOT EXISTS tenant.theme_settings (
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
