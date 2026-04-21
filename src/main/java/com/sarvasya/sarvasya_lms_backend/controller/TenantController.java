package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.TenantConfig;
import com.sarvasya.sarvasya_lms_backend.repository.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.repository.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantConfigRepository tenantConfigRepository;
    private final GlobalUserRepository globalUserRepository;
    private final JwtUtil jwtUtil;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TenantConfig>> getAllTenants() {
        return ResponseEntity.ok(tenantConfigRepository.findAll());
    }

    @GetMapping("/{tenantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TenantConfig> getTenantConfig(@PathVariable String tenantId) {
        return tenantConfigRepository.findById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> updateTenantConfig(@RequestBody TenantConfig config) {
        String tenantId = config.getTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "tenantId is required in the request body."));
        }
        if (!tenantConfigRepository.existsById(tenantId)) {
            return ResponseEntity.notFound().build();
        }
        tenantConfigRepository.save(config);
        return ResponseEntity.ok(Map.of("message", "Tenant configuration updated successfully."));
    }

    @PostMapping("/{tenantId}/impersonate")
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> impersonateTenant(@PathVariable String tenantId) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }

            // Extract the email from the principal
            Object principal = authentication.getPrincipal();
            String currentUserEmail = (principal instanceof UserDetails) 
                    ? ((UserDetails) principal).getUsername() 
                    : authentication.getName();

            if (currentUserEmail == null || currentUserEmail.isBlank()) {
                return ResponseEntity.status(401).body(Map.of("error", "Could not extract user email"));
            }

            // Global users exist in the "tenant" schema. GlobalUserRepository handles this automatically.
            var currentUser = globalUserRepository.findByEmail(currentUserEmail).orElse(null);

            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not found: " + currentUserEmail));
            }

            // Validate tenant exists
            if (!tenantConfigRepository.existsById(tenantId)) {
                return ResponseEntity.status(404).body(Map.of("error", "Tenant not found"));
            }

            // Generate impersonation token with target tenant context
            String impersonationToken = jwtUtil.generateImpersonationToken(
                    currentUserEmail,
                    tenantId,
                    currentUser.getRole().getValue());

            Map<String, Object> response = new HashMap<>();
            response.put("token", impersonationToken);
            response.put("impersonationToken", impersonationToken);
            response.put("tenantId", tenantId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
