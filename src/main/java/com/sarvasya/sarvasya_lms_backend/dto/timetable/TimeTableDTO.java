package com.sarvasya.sarvasya_lms_backend.dto.timetable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableDTO {
    private UUID id;
    private UUID classId;
    private String startDate;
    private String endDate;
    private Map<String, List<PeriodDTO>> schedule;

    @Getter
@Setter
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








