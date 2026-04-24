package com.sarvasya.sarvasya_lms_backend.dto;

import com.sarvasya.sarvasya_lms_backend.model.AttendanceStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AttendanceUpsertRequest {
    private UUID classId;
    private UUID courseId;
    private LocalDate attendanceDate;
    private String notes;
    private List<RecordRequest> records;

    @Data
    public static class RecordRequest {
        private UUID studentId;
        private AttendanceStatus status;
        private String remarks;
    }
}
