-- V3__Attempts_Answers_Certificates_Persistence.sql
-- Add attempt status + timestamps, and make answers/certificates queryable + idempotent.

-- Attempts: add status and lifecycle timestamps
ALTER TABLE tenant.sarvasya_attempt
    ADD COLUMN IF NOT EXISTS status VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED';

ALTER TABLE tenant.sarvasya_attempt
    ADD COLUMN IF NOT EXISTS started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tenant.sarvasya_attempt
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- submitted_at should represent actual submission time; keep column but remove default if present
DO $$
BEGIN
    -- Works on Postgres; ignore if already dropped
    BEGIN
        ALTER TABLE tenant.sarvasya_attempt ALTER COLUMN submitted_at DROP DEFAULT;
    EXCEPTION
        WHEN others THEN
            NULL;
    END;
END $$;

-- Backfill status for existing rows
UPDATE tenant.sarvasya_attempt
SET status = CASE
    WHEN score IS NULL THEN 'IN_PROGRESS'
    ELSE 'SUBMITTED'
END
WHERE status IS NULL OR status NOT IN ('IN_PROGRESS', 'SUBMITTED');

-- Student answers: prevent duplicates per attempt+question
DO $$
BEGIN
    BEGIN
        ALTER TABLE tenant.sarvasya_student_answer
            ADD CONSTRAINT uq_student_answer_attempt_question UNIQUE (attempt_id, question_id);
    EXCEPTION
        WHEN duplicate_object THEN
            NULL;
    END;
END $$;

-- Certificates: one certificate per student+course
DO $$
BEGIN
    BEGIN
        ALTER TABLE tenant.sarvasya_certificate
            ADD CONSTRAINT uq_certificate_student_course UNIQUE (student_id, course_id);
    EXCEPTION
        WHEN duplicate_object THEN
            NULL;
    END;
END $$;

