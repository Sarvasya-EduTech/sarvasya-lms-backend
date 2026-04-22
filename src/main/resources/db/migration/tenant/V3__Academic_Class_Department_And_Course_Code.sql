ALTER TABLE classes
    ADD COLUMN IF NOT EXISTS department_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'classes'
          AND constraint_name = 'fk_classes_department'
    ) THEN
        ALTER TABLE classes
            ADD CONSTRAINT fk_classes_department
            FOREIGN KEY (department_id) REFERENCES department(id);
    END IF;
END $$;
