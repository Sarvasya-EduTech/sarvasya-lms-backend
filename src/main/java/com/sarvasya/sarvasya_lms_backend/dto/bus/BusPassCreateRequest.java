package com.sarvasya.sarvasya_lms_backend.dto.bus;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusPassCreateRequest {
    private String userName;
    private UUID busId;
    private String stopId;
    private LocalDate validFrom;
    private LocalDate validTo;
}








