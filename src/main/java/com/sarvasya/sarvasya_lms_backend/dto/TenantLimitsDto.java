package com.sarvasya.sarvasya_lms_backend.dto;

import lombok.Data;

@Data
public class TenantLimitsDto {
    private String tenantId;
    private Long userLimit;
    private Long professorLimit;
    private Long adminLimit;
}
