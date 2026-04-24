CREATE TABLE IF NOT EXISTS professor_course_class_assignments (
    id UUID PRIMARY KEY,
    professor_id UUID NOT NULL,
    class_id UUID NOT NULL,
    course_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_professor_class_course_assignment
    ON professor_course_class_assignments (professor_id, class_id, course_id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'professor_course_class_assignments'
          AND constraint_name = 'fk_prof_assign_professor'
    ) THEN
        ALTER TABLE professor_course_class_assignments
            ADD CONSTRAINT fk_prof_assign_professor
            FOREIGN KEY (professor_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'professor_course_class_assignments'
          AND constraint_name = 'fk_prof_assign_class'
    ) THEN
        ALTER TABLE professor_course_class_assignments
            ADD CONSTRAINT fk_prof_assign_class
            FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'professor_course_class_assignments'
          AND constraint_name = 'fk_prof_assign_course'
    ) THEN
        ALTER TABLE professor_course_class_assignments
            ADD CONSTRAINT fk_prof_assign_course
            FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS attendance_sessions (
    id UUID PRIMARY KEY,
    class_id UUID NOT NULL,
    course_id UUID NOT NULL,
    professor_id UUID NOT NULL,
    attendance_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_attendance_session_per_day
    ON attendance_sessions (class_id, course_id, attendance_date);

CREATE INDEX IF NOT EXISTS idx_attendance_sessions_professor
    ON attendance_sessions (professor_id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'attendance_sessions'
          AND constraint_name = 'fk_attendance_session_class'
    ) THEN
        ALTER TABLE attendance_sessions
            ADD CONSTRAINT fk_attendance_session_class
            FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'attendance_sessions'
          AND constraint_name = 'fk_attendance_session_course'
    ) THEN
        ALTER TABLE attendance_sessions
            ADD CONSTRAINT fk_attendance_session_course
            FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'attendance_sessions'
          AND constraint_name = 'fk_attendance_session_professor'
    ) THEN
        ALTER TABLE attendance_sessions
            ADD CONSTRAINT fk_attendance_session_professor
            FOREIGN KEY (professor_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS attendance_records (
    id UUID PRIMARY KEY,
    attendance_session_id UUID NOT NULL,
    student_id UUID NOT NULL,
    status VARCHAR(16) NOT NULL,
    remarks TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_attendance_record_session_student
    ON attendance_records (attendance_session_id, student_id);

CREATE INDEX IF NOT EXISTS idx_attendance_records_student
    ON attendance_records (student_id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'attendance_records'
          AND constraint_name = 'fk_attendance_record_session'
    ) THEN
        ALTER TABLE attendance_records
            ADD CONSTRAINT fk_attendance_record_session
            FOREIGN KEY (attendance_session_id) REFERENCES attendance_sessions(id) ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'attendance_records'
          AND constraint_name = 'fk_attendance_record_student'
    ) THEN
        ALTER TABLE attendance_records
            ADD CONSTRAINT fk_attendance_record_student
            FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;
