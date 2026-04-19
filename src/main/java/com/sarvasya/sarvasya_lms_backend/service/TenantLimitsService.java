package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.TenantLimits;
import com.sarvasya.sarvasya_lms_backend.repository.TenantLimitsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantLimitsService {

    private final TenantLimitsRepository tenantLimitsRepository;

    @Value("${app.tenant.limits.users:1000}")
    private long defaultUserLimit;

    @Value("${app.tenant.limits.professors:150}")
    private long defaultProfessorLimit;

    @Value("${app.tenant.limits.admins:10}")
    private long defaultAdminLimit;

    public TenantLimits getLimits() {
        TenantLimits limits = tenantLimitsRepository.findFirstByOrderByIdAsc();
        if (limits == null) {
            limits = TenantLimits.builder()
                    .userLimit(defaultUserLimit)
                    .professorLimit(defaultProfessorLimit)
                    .adminLimit(defaultAdminLimit)
                    .build();
            return tenantLimitsRepository.save(limits);
        }
        return limits;
    }
}
