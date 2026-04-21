-- V12__Add_result_table.sql
CREATE TABLE IF NOT EXISTS results (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    student_name VARCHAR(255),
    exam_id UUID NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    marks_obtained INTEGER,
    total_marks INTEGER,
    grade VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
