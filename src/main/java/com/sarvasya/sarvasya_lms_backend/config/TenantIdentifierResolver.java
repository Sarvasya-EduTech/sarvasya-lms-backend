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
        return "public"; // fall back to public — no tenant context (e.g. during login)
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false; // prevent session re-validation when tenant changes
    }
}
