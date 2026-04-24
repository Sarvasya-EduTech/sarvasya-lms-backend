package com.sarvasya.sarvasya_lms_backend.dto.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.BusPass.BusPassStatus;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import com.sarvasya.sarvasya_lms_backend.model.bus.BusPass;

@Getter
@Setter
public class BusPassUpdateRequest {
    private UUID userId;
    private UUID busId;
    private String stopId;
    private LocalDate validFrom;
    private LocalDate validTo;
    private BusPassStatus status;
}
