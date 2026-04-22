-- Make fee structures class-specific (optional for legacy rows)
ALTER TABLE fee_structure
    ADD COLUMN IF NOT EXISTS class_id UUID;

CREATE INDEX IF NOT EXISTS idx_fee_structure_class
    ON fee_structure (class_id);

-- Keep existing scope index useful (degree/department/semester)
-- Optionally, future queries can use (class_id, semester, is_active) too.
CREATE INDEX IF NOT EXISTS idx_fee_structure_class_sem_active
    ON fee_structure (class_id, semester, is_active);

