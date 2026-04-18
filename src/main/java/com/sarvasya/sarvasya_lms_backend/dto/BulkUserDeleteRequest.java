package com.sarvasya.sarvasya_lms_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BulkUserDeleteRequest {
    @NotEmpty
    private List<UUID> ids;
}
