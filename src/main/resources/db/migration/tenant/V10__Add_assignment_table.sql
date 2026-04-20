CREATE TABLE assignment (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    course_id UUID,
    total_marks INT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
