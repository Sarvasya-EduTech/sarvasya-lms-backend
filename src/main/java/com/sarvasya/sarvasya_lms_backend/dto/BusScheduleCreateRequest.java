package com.sarvasya.sarvasya_lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class BusScheduleCreateRequest {
    private UUID busId;
    private String routeName;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    private List<RouteStopRequest> stops;

    @Data
    public static class RouteStopRequest {
        private String id;
        private String stopName;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime arrivalTime;
    }
}
