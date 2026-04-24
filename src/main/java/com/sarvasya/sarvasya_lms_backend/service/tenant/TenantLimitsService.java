package com.sarvasya.sarvasya_lms_backend.service.tenant;

import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantLimitsDto;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantLimits;

public interface TenantLimitsService {
    TenantLimits getLimits();
    TenantLimits getLimitsForTenant(String tenantId);
    TenantLimits updateLimits(TenantLimitsDto dto);
}
