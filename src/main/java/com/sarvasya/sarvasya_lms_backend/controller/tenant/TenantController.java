package com.sarvasya.sarvasya_lms_backend.controller.tenant;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantConfigUpdateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.TenantImpersonationResponse;
import com.sarvasya.sarvasya_lms_backend.model.tenant.TenantConfig;
import com.sarvasya.sarvasya_lms_backend.service.tenant.TenantService;

@RestController
@RequestMapping("/sarvasya/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TenantConfig>> getAllTenants() {
        return ResponseEntity.ok(tenantService.listTenants());
    }

    @GetMapping("/{tenantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TenantConfig> getTenantConfig(@PathVariable String tenantId) {
        return ResponseEntity.ok(tenantService.getTenant(tenantId));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<TenantConfig> updateTenantConfig(@Valid @RequestBody TenantConfigUpdateRequest request) {
        return ResponseEntity.ok(tenantService.updateTenantConfig(request));
    }

    @PostMapping("/{tenantId}/impersonate")
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> impersonateTenant(@PathVariable String tenantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) throw new IllegalArgumentException("Not authenticated");

        Object principal = authentication.getPrincipal();
        String currentUserEmail = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : authentication.getName();

        if (currentUserEmail == null || currentUserEmail.isBlank()) throw new IllegalArgumentException("Could not extract user email");
        TenantImpersonationResponse response = tenantService.impersonate(tenantId, currentUserEmail);
        return ResponseEntity.ok(response);
    }
}








