package com.sarvasya.sarvasya_lms_backend.service.impl.tenant;

import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantLimitsDto;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantConfig;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantLimits;
import com.sarvasya.sarvasya_lms_backend.repository.tenant.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import com.sarvasya.sarvasya_lms_backend.service.tenant.TenantLimitsService;

@Service
@RequiredArgsConstructor
public class TenantLimitsServiceImpl implements TenantLimitsService {

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

        // TenantConfig has schema = "tenant" defined, so we don't need manual schema switching
        return tenantConfigRepository.findById(tenantId)
                .map(config -> {
                    Map<String, Object> limitsMap = config.getLimits();
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
    }

    public TenantLimits updateLimits(TenantLimitsDto dto) {
        final String targetTenantId = (dto.getTenantId() != null && !dto.getTenantId().isBlank())
                ? dto.getTenantId()
                : TenantContext.getTenantId();

        if (targetTenantId == null || targetTenantId.equals("tenant") || targetTenantId.equals("public")) {
            throw new IllegalStateException("Cannot update limits for system schemas.");
        }

        TenantConfig config = tenantConfigRepository.findById(targetTenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant config not found for ID: " + targetTenantId));

        Map<String, Object> limitsMap = new HashMap<>(config.getLimits());
        if (dto.getUserLimit() != null)
            limitsMap.put("users", dto.getUserLimit());
        if (dto.getProfessorLimit() != null)
            limitsMap.put("professors", dto.getProfessorLimit());
        if (dto.getAdminLimit() != null)
            limitsMap.put("admins", dto.getAdminLimit());

        config.setLimits(limitsMap);
        tenantConfigRepository.save(config);

        return getLimitsForTenant(targetTenantId);
    }
}









