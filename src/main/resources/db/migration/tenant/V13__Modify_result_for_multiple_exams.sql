-- V13__Modify_result_for_multiple_exams.sql
ALTER TABLE results DROP COLUMN IF EXISTS exam_id;

CREATE TABLE IF NOT EXISTS result_items (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    result_id UUID NOT NULL REFERENCES results(id) ON DELETE CASCADE,
    exam_id UUID NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    marks_obtained INTEGER,
    total_marks INTEGER
);
