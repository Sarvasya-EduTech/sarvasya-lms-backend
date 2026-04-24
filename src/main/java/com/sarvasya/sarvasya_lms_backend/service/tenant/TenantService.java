package com.sarvasya.sarvasya_lms_backend.service.tenant;

import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantConfigUpdateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantImpersonationResponse;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantConfig;

import java.util.List;

public interface TenantService {
    List<TenantConfig> listTenants();

    TenantConfig getTenant(String tenantId);

    TenantConfig updateTenantConfig(TenantConfigUpdateRequest request);

    TenantImpersonationResponse impersonate(String tenantId, String currentUserEmail);
}


