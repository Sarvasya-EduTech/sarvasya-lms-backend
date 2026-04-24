package com.sarvasya.sarvasya_lms_backend.dto.tenant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantLimitsDto {
    private String tenantId;
    private Long userLimit;
    private Long professorLimit;
    private Long adminLimit;
}








