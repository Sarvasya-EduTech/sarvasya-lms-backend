-- V11__Add_admit_card_tables.sql
CREATE TABLE IF NOT EXISTS admit_card (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    class_id UUID NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admit_card_exam (
    admit_card_id UUID NOT NULL REFERENCES admit_card(id) ON DELETE CASCADE,
    exam_id UUID NOT NULL REFERENCES exam(id) ON DELETE CASCADE,
    PRIMARY KEY (admit_card_id, exam_id)
);
