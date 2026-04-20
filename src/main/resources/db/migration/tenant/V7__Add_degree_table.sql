-- V7__Add_degree_table.sql
-- Migration to add Degree table and link it to students

CREATE TABLE IF NOT EXISTS degree (
    id              UUID         NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    degree_name     VARCHAR(255) NOT NULL,
    no_of_years     INTEGER      NOT NULL,
    no_of_semesters INTEGER      NOT NULL,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookups by degree name
CREATE INDEX IF NOT EXISTS idx_degree_name ON degree (degree_name);

-- Add degree_id to users table (nullable — only relevant for students)
ALTER TABLE users ADD COLUMN IF NOT EXISTS degree_id UUID;

ALTER TABLE users
    ADD CONSTRAINT fk_user_degree FOREIGN KEY (degree_id) REFERENCES degree(id) ON DELETE SET NULL;

-- Index for filtering users by degree
CREATE INDEX IF NOT EXISTS idx_users_degree ON users (degree_id);
