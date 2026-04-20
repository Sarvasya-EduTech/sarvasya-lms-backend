package com.sarvasya.sarvasya_lms_backend.dto;

import com.sarvasya.sarvasya_lms_backend.model.BusPass.BusPassStatus;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BusPassUpdateRequest {
    private UUID userId;
    private UUID busId;
    private String stopId;
    private LocalDate validFrom;
    private LocalDate validTo;
    private BusPassStatus status;
}
