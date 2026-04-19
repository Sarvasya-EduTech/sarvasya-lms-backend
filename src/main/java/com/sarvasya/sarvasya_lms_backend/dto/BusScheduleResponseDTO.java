package com.sarvasya.sarvasya_lms_backend.dto;

import com.sarvasya.sarvasya_lms_backend.model.BusSchedule;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class BusScheduleResponseDTO {
    private UUID id;
    private BusInfo bus;
    private String routeName;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class BusInfo {
        private UUID id;
        private String busNumber;
    }

    public static BusScheduleResponseDTO fromEntity(BusSchedule schedule) {
        return BusScheduleResponseDTO.builder()
                .id(schedule.getId())
                .bus(BusInfo.builder()
                        .id(schedule.getBus().getId())
                        .busNumber(schedule.getBus().getBusNumber())
                        .build())
                .routeName(schedule.getRouteName())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .createdAt(schedule.getCreatedAt())
                .build();
    }
}
