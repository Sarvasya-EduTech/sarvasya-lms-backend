CREATE TABLE time_tables (
    id UUID PRIMARY KEY,
    class_id UUID NOT NULL,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE time_table_entries (
    id UUID PRIMARY KEY,
    time_table_id UUID NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time VARCHAR(20),
    end_time VARCHAR(20),
    course_name VARCHAR(255),
    CONSTRAINT fk_time_table FOREIGN KEY (time_table_id) REFERENCES time_tables(id) ON DELETE CASCADE
);
