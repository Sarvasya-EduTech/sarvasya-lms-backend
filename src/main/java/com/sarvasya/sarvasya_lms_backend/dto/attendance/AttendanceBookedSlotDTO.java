package com.sarvasya.sarvasya_lms_backend.dto.attendance;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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








