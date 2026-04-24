-- Add indexes for common tenant-scoped repository lookups.
-- Uses IF NOT EXISTS to keep migration idempotent.

CREATE INDEX IF NOT EXISTS idx_users_class_id
    ON users (class_id);

CREATE INDEX IF NOT EXISTS idx_users_role
    ON users (role);

CREATE INDEX IF NOT EXISTS idx_users_department
    ON users (department_id);

CREATE INDEX IF NOT EXISTS idx_classes_course
    ON classes (course_id);

CREATE INDEX IF NOT EXISTS idx_exam_course
    ON exam (course_id);

CREATE INDEX IF NOT EXISTS idx_results_student
    ON results (student_id);

CREATE INDEX IF NOT EXISTS idx_time_tables_class
    ON time_tables (class_id);

CREATE INDEX IF NOT EXISTS idx_time_table_entries_table_day_start
    ON time_table_entries (time_table_id, day_of_week, start_time);

CREATE INDEX IF NOT EXISTS idx_degree_department_mapping_degree
    ON degree_department_mapping (degree_id);

CREATE INDEX IF NOT EXISTS idx_fee_component_structure
    ON fee_component (fee_structure_id);

CREATE INDEX IF NOT EXISTS idx_student_fee_record_fee_structure_student
    ON student_fee_record (fee_structure_id, student_id);

CREATE INDEX IF NOT EXISTS idx_bus_passes_user
    ON bus_passes (user_id);

CREATE INDEX IF NOT EXISTS idx_bus_passes_bus
    ON bus_passes (bus_id);

CREATE INDEX IF NOT EXISTS idx_bus_schedules_bus
    ON bus_schedules (bus_id);

CREATE INDEX IF NOT EXISTS idx_bus_schedule_stops_schedule_sequence
    ON bus_schedule_stops (schedule_id, sequence_number);

CREATE INDEX IF NOT EXISTS idx_admit_card_class
    ON admit_card (class_id);
