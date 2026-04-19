package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.TenantLimits;
import com.sarvasya.sarvasya_lms_backend.repository.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.model.TenantConfig;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantLimitsService {

    private final TenantConfigRepository tenantConfigRepository;

    private static final long DEFAULT_USER_LIMIT = 1000L;
    private static final long DEFAULT_PROFESSOR_LIMIT = 150L;
    private static final long DEFAULT_ADMIN_LIMIT = 10L;

    public TenantLimits getLimits() {
        return getLimitsForTenant(TenantContext.getTenantId());
    }

    public TenantLimits getLimitsForTenant(String tenantId) {
        if (tenantId == null || tenantId.equals("tenant") || tenantId.equals("public")) {
            return TenantLimits.builder()
                    .userLimit(DEFAULT_USER_LIMIT)
                    .professorLimit(DEFAULT_PROFESSOR_LIMIT)
                    .adminLimit(DEFAULT_ADMIN_LIMIT)
                    .build();
        }

        String originalTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId("tenant");
            return tenantConfigRepository.findById(tenantId)
                    .map(config -> {
                        java.util.Map<String, Object> limitsMap = config.getLimits();
                        return TenantLimits.builder()
                                .userLimit(((Number) limitsMap.getOrDefault("users", DEFAULT_USER_LIMIT)).longValue())
                                .professorLimit(((Number) limitsMap.getOrDefault("professors", DEFAULT_PROFESSOR_LIMIT))
                                        .longValue())
                                .adminLimit(((Number) limitsMap.getOrDefault("admins", DEFAULT_ADMIN_LIMIT)).longValue())
                                .build();
                    })
                    .orElse(TenantLimits.builder()
                            .userLimit(DEFAULT_USER_LIMIT)
                            .professorLimit(DEFAULT_PROFESSOR_LIMIT)
                            .adminLimit(DEFAULT_ADMIN_LIMIT)
                            .build());
        } finally {
            TenantContext.setTenantId(originalTenant);
        }
    }

    public TenantLimits updateLimits(com.sarvasya.sarvasya_lms_backend.dto.TenantLimitsDto dto) {
        final String targetTenantId = (dto.getTenantId() != null && !dto.getTenantId().isBlank())
                ? dto.getTenantId()
                : TenantContext.getTenantId();

        if (targetTenantId == null || targetTenantId.equals("tenant") || targetTenantId.equals("public")) {
            throw new IllegalStateException("Cannot update limits for system schemas.");
        }

        String originalTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId("tenant");
            TenantConfig config = tenantConfigRepository.findById(targetTenantId)
                    .orElseThrow(() -> new IllegalArgumentException("Tenant config not found for ID: " + targetTenantId));

            java.util.Map<String, Object> limitsMap = new java.util.HashMap<>(config.getLimits());
            if (dto.getUserLimit() != null)
                limitsMap.put("users", dto.getUserLimit());
            if (dto.getProfessorLimit() != null)
                limitsMap.put("professors", dto.getProfessorLimit());
            if (dto.getAdminLimit() != null)
                limitsMap.put("admins", dto.getAdminLimit());

            config.setLimits(limitsMap);
            tenantConfigRepository.save(config);
        } finally {
            TenantContext.setTenantId(originalTenant);
        }

        return getLimitsForTenant(targetTenantId);
    }
}
