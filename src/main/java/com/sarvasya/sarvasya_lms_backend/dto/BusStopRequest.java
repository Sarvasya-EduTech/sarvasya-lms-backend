package com.sarvasya.sarvasya_lms_backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BusStopRequest {
    private UUID busId;
    private String stopName;
}
