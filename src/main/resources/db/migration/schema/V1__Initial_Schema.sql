-- V1__Initial_Schema.sql
-- Consolidated schema for the shared 'tenant' schema

CREATE SCHEMA IF NOT EXISTS tenant;

-- 1. Configuration and Global Users
CREATE TABLE IF NOT EXISTS tenant.tenant_config (
    tenant_id VARCHAR(255) PRIMARY KEY,
    features JSONB,
    limits JSONB,
    license JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.users (
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



-- 2. LMS Core Tables
CREATE TABLE IF NOT EXISTS tenant.sarvasya_course (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL,
    course_code VARCHAR(255) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_module (
    id UUID PRIMARY KEY,
    course_id UUID REFERENCES tenant.sarvasya_course(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    order_index INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_lesson (
    id UUID PRIMARY KEY,
    module_id UUID REFERENCES tenant.sarvasya_module(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    video_url TEXT,
    order_index INT,
    is_preview BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_study_material (
    id UUID PRIMARY KEY,
    module_id UUID REFERENCES tenant.sarvasya_module(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    file_url TEXT,
    type VARCHAR(50),
    order_index INT,
    is_downloadable BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_quiz (
    id UUID PRIMARY KEY,
    module_id UUID REFERENCES tenant.sarvasya_module(id) ON DELETE CASCADE,
    title VARCHAR(255),
    description TEXT,
    duration_minutes INTEGER,
    passing_score INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_exam (
    id UUID PRIMARY KEY,
    course_id UUID REFERENCES tenant.sarvasya_course(id) ON DELETE CASCADE,
    title VARCHAR(255),
    description TEXT,
    duration_minutes INTEGER,
    passing_score INT DEFAULT 80,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_question (
    id UUID PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    question_text TEXT,
    explanation TEXT,
    marks DECIMAL,
    negative_marks DECIMAL,
    nat_answer DECIMAL,
    nat_min_range DECIMAL,
    nat_max_range DECIMAL,
    is_range_based BOOLEAN DEFAULT FALSE,
    topic VARCHAR(255),
    difficulty VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_option (
    id UUID PRIMARY KEY,
    question_id UUID REFERENCES tenant.sarvasya_question(id) ON DELETE CASCADE,
    text VARCHAR(255) NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_quiz_question (
    id UUID PRIMARY KEY,
    quiz_id UUID REFERENCES tenant.sarvasya_quiz(id) ON DELETE CASCADE,
    question_id UUID REFERENCES tenant.sarvasya_question(id) ON DELETE CASCADE,
    order_index INT
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_exam_question (
    id UUID PRIMARY KEY,
    exam_id UUID REFERENCES tenant.sarvasya_exam(id) ON DELETE CASCADE,
    question_id UUID REFERENCES tenant.sarvasya_question(id) ON DELETE CASCADE,
    order_index INT
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_attempt (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    assessment_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    score DECIMAL,
    max_score DECIMAL,
    percentage DECIMAL,
    is_passed BOOLEAN DEFAULT FALSE,
    total_questions INT,
    correct_answers INT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_student_answer (
    id UUID PRIMARY KEY,
    attempt_id UUID REFERENCES tenant.sarvasya_attempt(id) ON DELETE CASCADE,
    question_id UUID REFERENCES tenant.sarvasya_question(id) ON DELETE CASCADE,
    selected_option_ids TEXT,
    nat_answer_given DECIMAL,
    is_correct BOOLEAN DEFAULT FALSE,
    marks_awarded DECIMAL
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_enrollment (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    course_id UUID REFERENCES tenant.sarvasya_course(id) ON DELETE CASCADE,
    enrollment_number VARCHAR(255) UNIQUE,
    sequence_number BIGINT,
    is_paid BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, course_id)
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_payment (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    course_id UUID REFERENCES tenant.sarvasya_course(id) ON DELETE CASCADE,
    amount DECIMAL,
    payment_gateway VARCHAR(100),
    payment_id VARCHAR(255),
    status VARCHAR(50),
    paid_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_invoice (
    id UUID PRIMARY KEY,
    payment_id UUID REFERENCES tenant.sarvasya_payment(id) ON DELETE CASCADE,
    invoice_number VARCHAR(255) UNIQUE,
    invoice_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tenant.sarvasya_certificate (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    course_id UUID REFERENCES tenant.sarvasya_course(id) ON DELETE CASCADE,
    certificate_url TEXT,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
