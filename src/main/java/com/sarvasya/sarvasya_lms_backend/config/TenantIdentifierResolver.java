package com.sarvasya.sarvasya_lms_backend.config;

import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId;
        }
        return "tenant"; // default schema
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
