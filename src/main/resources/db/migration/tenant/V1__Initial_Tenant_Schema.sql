-- V1__Initial_Tenant_Schema.sql
-- Consolidated schema for individual tenant schemas

-- 1. UUIDv7 Generator Function
CREATE OR REPLACE FUNCTION uuid_generate_v7()
RETURNS uuid
AS $$
DECLARE
  unix_ts_ms bytea;
  uuid_bytes bytea;
BEGIN
  unix_ts_ms = substring(int8send(floor(extract(epoch from clock_timestamp()) * 1000)::bigint) from 3);
  uuid_bytes = gen_random_bytes(10);
  uuid_bytes = set_byte(uuid_bytes, 0, (get_byte(uuid_bytes, 0) & 15) | 112);
  RETURN encode(unix_ts_ms || uuid_bytes, 'hex')::uuid;
END
$$
LANGUAGE plpgsql
VOLATILE;

-- 2. User and Theme Tables
CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    requires_password_change BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    class_id UUID,
    department_id UUID,
    degree_id UUID
);

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

-- 3. Academic Structure
CREATE TABLE IF NOT EXISTS department (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS degree (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    degree_name VARCHAR(255) NOT NULL,
    no_of_years INTEGER NOT NULL,
    no_of_semesters INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    college_id VARCHAR(255),
    department_id UUID NOT NULL REFERENCES department(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS classes (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    batch_id UUID,
    teacher_id UUID,
    course_id UUID REFERENCES course(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS class_course (
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    PRIMARY KEY (class_id, course_id)
);

CREATE TABLE IF NOT EXISTS professor_class (
    professor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    PRIMARY KEY (professor_id, class_id)
);

-- 4. Calendar and Exams
CREATE TABLE IF NOT EXISTS exam (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    course_id UUID REFERENCES course(id),
    batch_id UUID,
    total_marks INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS calendar_item (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(255) NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    all_day BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id UUID,
    reference_type VARCHAR(255),
    color_code VARCHAR(255),
    class_id UUID REFERENCES classes(id),
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assignment (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    course_id UUID,
    total_marks INT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- 5. Results and Admit Cards
CREATE TABLE IF NOT EXISTS results (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    student_name VARCHAR(255),
    class_name VARCHAR(255),
    marks_obtained INTEGER,
    total_marks INTEGER,
    grade VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS result_items (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    result_id UUID NOT NULL REFERENCES results(id) ON DELETE CASCADE,
    exam_id UUID NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    marks_obtained INTEGER,
    total_marks INTEGER
);

CREATE TABLE IF NOT EXISTS admit_card (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admit_card_exam (
    admit_card_id UUID NOT NULL REFERENCES admit_card(id) ON DELETE CASCADE,
    exam_id UUID NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    PRIMARY KEY (admit_card_id, exam_id)
);

-- 6. Time Tables
CREATE TABLE IF NOT EXISTS time_tables (
    id UUID PRIMARY KEY,
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS time_table_entries (
    id UUID PRIMARY KEY,
    time_table_id UUID NOT NULL REFERENCES time_tables(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL,
    start_time VARCHAR(20),
    end_time VARCHAR(20),
    course_name VARCHAR(255),
    course_id UUID REFERENCES course(id) ON DELETE SET NULL
);

-- 7. Transport (Buses)
CREATE TABLE IF NOT EXISTS buses (
    id UUID NOT NULL PRIMARY KEY,
    bus_number VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bus_stops (
    id UUID NOT NULL PRIMARY KEY,
    bus_id UUID NOT NULL REFERENCES buses(id) ON DELETE CASCADE,
    stop_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_bus_stop UNIQUE (bus_id, stop_name)
);

CREATE TABLE IF NOT EXISTS bus_schedules (
    id UUID NOT NULL PRIMARY KEY,
    bus_id UUID NOT NULL REFERENCES buses(id) ON DELETE CASCADE,
    route_name VARCHAR(255) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bus_schedule_stops (
    id UUID NOT NULL PRIMARY KEY,
    schedule_id UUID NOT NULL REFERENCES bus_schedules(id) ON DELETE CASCADE,
    stop_id UUID NOT NULL REFERENCES bus_stops(id) ON DELETE CASCADE,
    arrival_time TIME NOT NULL,
    sequence_number INTEGER DEFAULT 0
);

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

-- 8. Additional constraints and indexes
ALTER TABLE users ADD CONSTRAINT fk_user_class FOREIGN KEY (class_id) REFERENCES classes(id);
ALTER TABLE users ADD CONSTRAINT fk_user_department FOREIGN KEY (department_id) REFERENCES department(id);
ALTER TABLE users ADD CONSTRAINT fk_user_degree FOREIGN KEY (degree_id) REFERENCES degree(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_degree_name ON degree (degree_name);
CREATE INDEX IF NOT EXISTS idx_users_degree ON users (degree_id);
CREATE INDEX IF NOT EXISTS idx_calendar_item_start_datetime ON calendar_item (start_date_time);
CREATE INDEX IF NOT EXISTS idx_calendar_item_type ON calendar_item (type);
CREATE INDEX IF NOT EXISTS idx_calendar_item_class_id ON calendar_item(class_id);
CREATE INDEX IF NOT EXISTS idx_course_department ON course (department_id);
CREATE INDEX IF NOT EXISTS idx_class_course ON class_course (class_id, course_id);
CREATE INDEX IF NOT EXISTS idx_professor_class ON professor_class (professor_id, class_id);
