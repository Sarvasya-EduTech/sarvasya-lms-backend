package com.sarvasya.sarvasya_lms_backend.controller.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantLimitsDto;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantLimits;
import com.sarvasya.sarvasya_lms_backend.service.tenant.TenantLimitsService;

@RestController
@RequestMapping("/sarvasya/limits")
@RequiredArgsConstructor
public class TenantLimitsController {

    private final TenantLimitsService tenantLimitsService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TenantLimits> getLimits(@RequestParam String tenantId) {
        return ResponseEntity.ok(tenantLimitsService.getLimitsForTenant(tenantId));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<TenantLimits> updateLimits(@RequestBody TenantLimitsDto dto) {
        return ResponseEntity.ok(tenantLimitsService.updateLimits(dto));
    }
}








