-- V14__Add_class_info_to_result.sql
ALTER TABLE results ADD COLUMN IF NOT EXISTS class_name VARCHAR(255);
