CREATE TABLE IF NOT EXISTS degree_department_mapping (
    id UUID PRIMARY KEY,
    degree_id UUID NOT NULL REFERENCES degree(id) ON DELETE CASCADE,
    department_id UUID NOT NULL REFERENCES department(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_degree_department UNIQUE (degree_id, department_id)
);

CREATE TABLE IF NOT EXISTS fee_structure (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    degree_id UUID NOT NULL REFERENCES degree(id) ON DELETE CASCADE,
    department_id UUID NOT NULL REFERENCES department(id) ON DELETE CASCADE,
    semester INTEGER NOT NULL,
    due_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fee_component (
    id UUID PRIMARY KEY,
    fee_structure_id UUID NOT NULL REFERENCES fee_structure(id) ON DELETE CASCADE,
    component_name VARCHAR(255) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    is_mandatory BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS student_fee_record (
    id UUID PRIMARY KEY,
    fee_structure_id UUID NOT NULL REFERENCES fee_structure(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    selected_component_ids TEXT,
    total_amount NUMERIC(12,2) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'UNPAID',
    payment_mode VARCHAR(32),
    receipt_number VARCHAR(64) UNIQUE,
    paid_at TIMESTAMP,
    offline_marked_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_fee_structure_scope
    ON fee_structure (degree_id, department_id, semester, is_active);

CREATE INDEX IF NOT EXISTS idx_student_fee_record_status
    ON student_fee_record (student_id, status);
