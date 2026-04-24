package com.sarvasya.sarvasya_lms_backend.dto.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.BusStop;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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








