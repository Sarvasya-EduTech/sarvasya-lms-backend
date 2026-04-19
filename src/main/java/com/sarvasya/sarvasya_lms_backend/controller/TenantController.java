package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.repository.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.model.TenantConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantConfigRepository tenantConfigRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getAllTenants() {
        List<String> tenantIds = tenantConfigRepository.findAll()
                .stream()
                .map(TenantConfig::getTenantId)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenantIds);
    }

    @GetMapping("/{tenantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TenantConfig> getTenantConfig(@org.springframework.web.bind.annotation.PathVariable String tenantId) {
        return tenantConfigRepository.findById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @org.springframework.web.bind.annotation.PutMapping
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> updateTenantConfig(@org.springframework.web.bind.annotation.RequestBody TenantConfig config) {
        String tenantId = config.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return ResponseEntity.badRequest().body("Error: tenantId is required in the request body.");
        }
        if (!tenantConfigRepository.existsById(tenantId)) {
            return ResponseEntity.notFound().build();
        }
        tenantConfigRepository.save(config);
        return ResponseEntity.ok("Tenant configuration updated successfully.");
    }
}
