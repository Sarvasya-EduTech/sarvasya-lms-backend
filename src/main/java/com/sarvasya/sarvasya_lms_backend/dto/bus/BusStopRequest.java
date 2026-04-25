package com.sarvasya.sarvasya_lms_backend.dto.bus;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusStopRequest {
    private UUID busId;
    private String stopName;
}








