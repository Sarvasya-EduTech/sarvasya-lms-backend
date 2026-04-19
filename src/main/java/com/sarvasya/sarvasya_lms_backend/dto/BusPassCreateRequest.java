package com.sarvasya.sarvasya_lms_backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BusPassCreateRequest {
    private String userName;
    private UUID busId;
    private LocalDate validFrom;
    private LocalDate validTo;
}
