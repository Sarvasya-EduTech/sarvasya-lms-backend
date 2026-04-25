package com.sarvasya.sarvasya_lms_backend.dto.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusScheduleCreateRequest {
    private UUID busId;
    private String routeName;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    private List<RouteStopRequest> stops;

    @Getter
@Setter
    public static class RouteStopRequest {
        private String id;
        private String stopName;
        @JsonFormat(pattern = "HH:mm:ss")
        private LocalTime arrivalTime;
    }
}








