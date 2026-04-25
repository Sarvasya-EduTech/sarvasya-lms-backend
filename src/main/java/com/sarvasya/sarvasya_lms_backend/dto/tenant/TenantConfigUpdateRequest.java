package com.sarvasya.sarvasya_lms_backend.dto.tenant;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantConfigUpdateRequest {
    @NotBlank
    private String tenantId;

    private Map<String, Object> features;
    private Map<String, Object> limits;
    private Map<String, Object> license;
}









