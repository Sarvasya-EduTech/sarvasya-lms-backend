package com.sarvasya.sarvasya_lms_backend.dto.user;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkUserDeleteRequest {
    @NotEmpty
    private List<UUID> ids;
}








