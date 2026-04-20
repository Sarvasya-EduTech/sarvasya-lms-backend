-- V3__Add_normalized_bus_tables.sql
-- Full recreation of all bus tables using IF NOT EXISTS.
-- Safe for both fresh tenants (V2 already created them) and
-- existing tenants where tables may have been dropped.

-- 1. Core buses table
CREATE TABLE IF NOT EXISTS buses (
    id UUID NOT NULL PRIMARY KEY,
    bus_number VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Master stop list per bus
CREATE TABLE IF NOT EXISTS bus_stops (
    id UUID NOT NULL PRIMARY KEY,
    bus_id UUID NOT NULL REFERENCES buses(id) ON DELETE CASCADE,
    stop_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_bus_stop UNIQUE (bus_id, stop_name)
);

-- 3. Bus schedules
CREATE TABLE IF NOT EXISTS bus_schedules (
    id UUID NOT NULL PRIMARY KEY,
    bus_id UUID NOT NULL REFERENCES buses(id) ON DELETE CASCADE,
    route_name VARCHAR(255) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Schedule-to-stop links with arrival times
CREATE TABLE IF NOT EXISTS bus_schedule_stops (
    id UUID NOT NULL PRIMARY KEY,
    schedule_id UUID NOT NULL REFERENCES bus_schedules(id) ON DELETE CASCADE,
    stop_id UUID NOT NULL REFERENCES bus_stops(id) ON DELETE CASCADE,
    arrival_time TIME NOT NULL,
    sequence_number INTEGER DEFAULT 0
);

-- 5. Bus passes
CREATE TABLE IF NOT EXISTS bus_passes (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bus_id UUID NOT NULL REFERENCES buses(id) ON DELETE CASCADE,
    stop_id UUID REFERENCES bus_stops(id) ON DELETE SET NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
