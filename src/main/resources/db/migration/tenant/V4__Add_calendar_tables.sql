-- V4__Add_calendar_tables.sql
-- Adds tables for the Calendar feature (CalendarItem, Classes, Exam)

-- Create UUIDv7 generator function
CREATE OR REPLACE FUNCTION uuid_generate_v7()
RETURNS uuid
AS $$
DECLARE
  unix_ts_ms bytea;
  uuid_bytes bytea;
BEGIN
  unix_ts_ms = substring(int8send(floor(extract(epoch from clock_timestamp()) * 1000)::bigint) from 3);
  uuid_bytes = gen_random_bytes(10);
  uuid_bytes = set_byte(uuid_bytes, 0, (get_byte(uuid_bytes, 0) & 15) | 112);
  RETURN encode(unix_ts_ms || uuid_bytes, 'hex')::uuid;
END
$$
LANGUAGE plpgsql
VOLATILE;

CREATE TABLE IF NOT EXISTS classes (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    batch_id UUID,
    teacher_id UUID,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exam (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    subject VARCHAR(255) NOT NULL,
    batch_id UUID,
    total_marks INTEGER,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS calendar_item (
    id UUID NOT NULL DEFAULT uuid_generate_v7() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(255) NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    all_day BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id UUID,
    reference_type VARCHAR(255),
    color_code VARCHAR(255),
    created_by UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_calendar_item_start_datetime ON calendar_item (start_date_time);
CREATE INDEX idx_calendar_item_type ON calendar_item (type);
