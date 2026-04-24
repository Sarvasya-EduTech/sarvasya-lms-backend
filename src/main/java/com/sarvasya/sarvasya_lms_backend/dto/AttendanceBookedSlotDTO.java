package com.sarvasya.sarvasya_lms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceBookedSlotDTO {
    private UUID timeTableId;
    private UUID timeTableEntryId;
    private UUID classId;
    private UUID courseId;
    private String courseName;
    private String dayOfWeek;
    private LocalDate slotDate;
    private String startTime;
    private String endTime;
}
