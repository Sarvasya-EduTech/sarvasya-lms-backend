package com.sarvasya.sarvasya_lms_backend.dto;

import com.sarvasya.sarvasya_lms_backend.model.BusStop;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class BusStopResponseDTO {
    private UUID id;
    private UUID busId;
    private String stopName;

    public static BusStopResponseDTO fromEntity(BusStop stop) {
        return BusStopResponseDTO.builder()
                .id(stop.getId())
                .busId(stop.getBus().getId())
                .stopName(stop.getStopName())
                .build();
    }
}
