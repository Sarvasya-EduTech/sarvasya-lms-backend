-- V9__Add_class_id_to_calendar_item.sql
-- Add class_id to calendar_item so CLASS, EXAM, ASSIGNMENT items can be linked to a specific academic class.
-- Students will only see calendar items linked to their assigned class (+ global EVENT/HOLIDAY items).

ALTER TABLE calendar_item ADD COLUMN IF NOT EXISTS class_id UUID REFERENCES classes(id);
CREATE INDEX IF NOT EXISTS idx_calendar_item_class_id ON calendar_item(class_id);
