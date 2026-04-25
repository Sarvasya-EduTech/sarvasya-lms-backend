package com.sarvasya.sarvasya_lms_backend.service.impl.tenant;

import com.sarvasya.sarvasya_lms_backend.model.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.api.NotFoundException;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantConfigUpdateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantImpersonationResponse;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantConfig;
import com.sarvasya.sarvasya_lms_backend.repository.tenant.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.repository.user.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import com.sarvasya.sarvasya_lms_backend.service.tenant.TenantService;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantConfigRepository tenantConfigRepository;
    private final GlobalUserRepository globalUserRepository;
    private final JwtUtil jwtUtil;

    @Override
    public List<TenantConfig> listTenants() {
        return tenantConfigRepository.findAll();
    }

    @Override
    public TenantConfig getTenant(String tenantId) {
        return tenantConfigRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
    }

    @Override
    @Transactional
    public TenantConfig updateTenantConfig(TenantConfigUpdateRequest request) {
        TenantConfig existing = tenantConfigRepository.findById(request.getTenantId())
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        existing.setFeatures(request.getFeatures());
        existing.setLimits(request.getLimits());
        existing.setLicense(request.getLicense());
        return tenantConfigRepository.save(existing);
    }

    @Override
    public TenantImpersonationResponse impersonate(String tenantId, String currentUserEmail) {
        var currentUser = globalUserRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!tenantConfigRepository.existsById(tenantId)) {
            throw new NotFoundException("Tenant not found");
        }

        String token = jwtUtil.generateImpersonationToken(
                currentUserEmail,
                tenantId,
                currentUser.getRole().getValue());

        return new TenantImpersonationResponse(tenantId, token);
    }
}









