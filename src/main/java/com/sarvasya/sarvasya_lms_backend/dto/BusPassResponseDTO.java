package com.sarvasya.sarvasya_lms_backend.dto;

import com.sarvasya.sarvasya_lms_backend.model.BusPass;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BusPassResponseDTO {
    private UUID id;
    private UserInfo user;
    private BusInfo bus;
    private String stopName;
    private String stopId;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String status;
    private LocalDateTime createdAt;

    @Data
    @Builder
    public static class UserInfo {
        private UUID id;
        private String name;
        private String email;
    }

    @Data
    @Builder
    public static class BusInfo {
        private UUID id;
        private String busNumber;
    }

    public static BusPassResponseDTO fromEntity(BusPass busPass) {
        return BusPassResponseDTO.builder()
                .id(busPass.getId())
                .user(UserInfo.builder()
                        .id(busPass.getUser().getId())
                        .name(busPass.getUser().getName())
                        .email(busPass.getUser().getEmail())
                        .build())
                .bus(BusInfo.builder()
                        .id(busPass.getBus().getId())
                        .busNumber(busPass.getBus().getBusNumber())
                        .build())
                .stopName(busPass.getStop() != null ? busPass.getStop().getStopName() : null)
                .stopId(busPass.getStop() != null ? busPass.getStop().getId().toString() : null)
                .validFrom(busPass.getValidFrom())
                .validTo(busPass.getValidTo())
                .status(busPass.getStatus().name())
                .createdAt(busPass.getCreatedAt())
                .build();
    }
}
