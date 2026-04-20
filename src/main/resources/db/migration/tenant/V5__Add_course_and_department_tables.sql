-- V5__Add_course_and_department_tables.sql
-- Migration to add Department, Course tables and relationships

-- Ensure uuid_generate_v7 function exists (created in V4)
-- Create Department table
CREATE TABLE IF NOT EXISTS department (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Course table
CREATE TABLE IF NOT EXISTS course (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    college_id UUID,
    department_id UUID NOT NULL REFERENCES department(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Update Classes table to reference Course
-- Update Classes table to reference Course
ALTER TABLE classes ADD COLUMN IF NOT EXISTS course_id UUID REFERENCES course(id);
-- Remove legacy subject column from classes
ALTER TABLE classes DROP COLUMN IF EXISTS subject;

-- Update Exam table to reference Course
ALTER TABLE exam ADD COLUMN IF NOT EXISTS course_id UUID REFERENCES course(id);
-- Remove legacy subject column from exam
ALTER TABLE exam DROP COLUMN IF EXISTS subject;

-- Join table: Class can have multiple Courses (many-to-many)
CREATE TABLE IF NOT EXISTS class_course (
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    PRIMARY KEY (class_id, course_id)
);

-- Join table: Professor (users with role PROFESSOR) can have multiple Classes
CREATE TABLE IF NOT EXISTS professor_class (
    professor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    PRIMARY KEY (professor_id, class_id)
);

-- Update Users table: add class_id (one class per user) and department_id for admins
ALTER TABLE users ADD COLUMN IF NOT EXISTS class_id UUID;
ALTER TABLE users ADD COLUMN IF NOT EXISTS department_id UUID;

-- Add foreign keys where applicable (no cascade to prevent accidental deletions)
ALTER TABLE users
    ADD CONSTRAINT fk_user_class FOREIGN KEY (class_id) REFERENCES classes(id),
    ADD CONSTRAINT fk_user_department FOREIGN KEY (department_id) REFERENCES department(id);

-- Indexes for performance
CREATE INDEX idx_course_department ON course (department_id);
CREATE INDEX idx_class_course ON class_course (class_id, course_id);
CREATE INDEX idx_professor_class ON professor_class (professor_id, class_id);
