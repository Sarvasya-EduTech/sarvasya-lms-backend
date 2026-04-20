-- V8__Add_subject_to_classes.sql
-- Add subject column back to classes table as it's used for Class Names in UI

ALTER TABLE classes ADD COLUMN IF NOT EXISTS subject VARCHAR(255);
ALTER TABLE exam ADD COLUMN IF NOT EXISTS subject VARCHAR(255);

-- Update existing rows if any
UPDATE classes SET subject = 'Unnamed Class' WHERE subject IS NULL;
UPDATE exam SET subject = 'Unnamed Exam' WHERE subject IS NULL;

-- Make it NOT NULL
ALTER TABLE classes ALTER COLUMN subject SET NOT NULL;
ALTER TABLE exam ALTER COLUMN subject SET NOT NULL;
