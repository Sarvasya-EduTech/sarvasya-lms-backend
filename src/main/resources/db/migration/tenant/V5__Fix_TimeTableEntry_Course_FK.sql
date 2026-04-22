-- Fix wrong FK on time_table_entries.course_id (should reference tenant course table)
DO $$
BEGIN
    -- Drop existing FK if it points somewhere else (name is stable in Postgres defaults)
    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        WHERE tc.constraint_schema = current_schema()
          AND tc.table_name = 'time_table_entries'
          AND tc.constraint_type = 'FOREIGN KEY'
          AND tc.constraint_name = 'time_table_entries_course_id_fkey'
    ) THEN
        ALTER TABLE time_table_entries
            DROP CONSTRAINT time_table_entries_course_id_fkey;
    END IF;

    -- Re-add correct FK to tenant-scoped course table
    ALTER TABLE time_table_entries
        ADD CONSTRAINT time_table_entries_course_id_fkey
        FOREIGN KEY (course_id) REFERENCES course(id)
        ON DELETE SET NULL;
END $$;

