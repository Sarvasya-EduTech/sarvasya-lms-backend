package com.sarvasya.sarvasya_lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sarvasya.sarvasya_lms_backend.model.BusSchedule;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BusScheduleResponseDTO {
    private UUID id;
    private BusInfo bus;
    private String routeName;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    private List<RouteStopDTO> stops;
    private java.time.LocalDateTime createdAt;

    @Data
    @Builder
    public static class RouteStopDTO {
        private UUID id; // Master Stop ID
        private String stopName;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime arrivalTime;
    }

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
                .stops(schedule.getScheduleStops() != null ? schedule.getScheduleStops().stream()
                        .map(ss -> RouteStopDTO.builder()
                                .id(ss.getStop().getId())
                                .stopName(ss.getStop().getStopName())
                                .arrivalTime(ss.getArrivalTime())
                                .build())
                        .toList() : null)
                .createdAt(schedule.getCreatedAt())
                .build();
    }
}
