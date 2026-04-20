-- V6__Alter_college_id_to_varchar.sql
-- ---------------------------------------------------------
-- This migration converts college_id from UUID to VARCHAR(255)
-- to allow for non-UUID strings.
-- ---------------------------------------------------------

DO $$ 
BEGIN 
    -- Check if the column is currently a UUID before altering
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='course' AND column_name='college_id' AND data_type='uuid') THEN
        ALTER TABLE course ALTER COLUMN college_id TYPE VARCHAR(255);
    END IF;
END $$;
