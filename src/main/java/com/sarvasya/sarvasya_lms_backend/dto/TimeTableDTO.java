package com.sarvasya.sarvasya_lms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableDTO {
    private UUID id;
    private UUID classId;
    private String startDate;
    private String endDate;
    private Map<String, List<PeriodDTO>> schedule;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodDTO {
        private UUID id;
        private UUID courseId;
        private String courseName;
        private String startTime;
        private String endTime;
    }
}
