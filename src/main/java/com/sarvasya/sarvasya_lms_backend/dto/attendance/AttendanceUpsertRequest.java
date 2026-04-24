package com.sarvasya.sarvasya_lms_backend.dto.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.attendance.AttendanceStatus;

public class AttendanceUpsertRequest {
    private UUID classId;
    private UUID courseId;
    private LocalDate attendanceDate;
    private String notes;
    private List<RecordRequest> records;

    public UUID getClassId() {
        return classId;
    }

    public void setClassId(UUID classId) {
        this.classId = classId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<RecordRequest> getRecords() {
        return records;
    }

    public void setRecords(List<RecordRequest> records) {
        this.records = records;
    }

    public static class RecordRequest {
        private UUID studentId;
        private AttendanceStatus status;
        private String remarks;

        public UUID getStudentId() {
            return studentId;
        }

        public void setStudentId(UUID studentId) {
            this.studentId = studentId;
        }

        public AttendanceStatus getStatus() {
            return status;
        }

        public void setStatus(AttendanceStatus status) {
            this.status = status;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
