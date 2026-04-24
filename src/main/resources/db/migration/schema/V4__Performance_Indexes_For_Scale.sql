-- Add high-impact indexes for read-heavy LMS flows at scale.
-- Safe to run repeatedly via IF NOT EXISTS.

CREATE INDEX IF NOT EXISTS idx_sarvasya_module_course_order
    ON tenant.sarvasya_module (course_id, order_index);

CREATE INDEX IF NOT EXISTS idx_sarvasya_lesson_module_order
    ON tenant.sarvasya_lesson (module_id, order_index);

CREATE INDEX IF NOT EXISTS idx_sarvasya_study_material_module_order
    ON tenant.sarvasya_study_material (module_id, order_index);

CREATE INDEX IF NOT EXISTS idx_sarvasya_quiz_module
    ON tenant.sarvasya_quiz (module_id);

CREATE INDEX IF NOT EXISTS idx_sarvasya_exam_course
    ON tenant.sarvasya_exam (course_id);

CREATE INDEX IF NOT EXISTS idx_sarvasya_quiz_question_quiz_order
    ON tenant.sarvasya_quiz_question (quiz_id, order_index);

CREATE INDEX IF NOT EXISTS idx_sarvasya_exam_question_exam_order
    ON tenant.sarvasya_exam_question (exam_id, order_index);

CREATE INDEX IF NOT EXISTS idx_sarvasya_option_question
    ON tenant.sarvasya_option (question_id);

CREATE INDEX IF NOT EXISTS idx_sarvasya_attempt_lookup_status_updated
    ON tenant.sarvasya_attempt (student_id, assessment_id, type, status, updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_sarvasya_attempt_lookup_updated
    ON tenant.sarvasya_attempt (student_id, assessment_id, type, updated_at DESC);

CREATE INDEX IF NOT EXISTS idx_sarvasya_enrollment_student
    ON tenant.sarvasya_enrollment (student_id);

CREATE INDEX IF NOT EXISTS idx_sarvasya_enrollment_course
    ON tenant.sarvasya_enrollment (course_id);

CREATE INDEX IF NOT EXISTS idx_sarvasya_payment_student_status_paid
    ON tenant.sarvasya_payment (student_id, status, paid_at DESC);

CREATE INDEX IF NOT EXISTS idx_sarvasya_payment_course
    ON tenant.sarvasya_payment (course_id);

CREATE INDEX IF NOT EXISTS idx_tenant_users_role
    ON tenant.users (role);

CREATE INDEX IF NOT EXISTS idx_tenant_users_class
    ON tenant.users (class_id);
