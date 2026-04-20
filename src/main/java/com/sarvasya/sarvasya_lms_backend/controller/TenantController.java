package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.repository.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.model.TenantConfig;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantConfigRepository tenantConfigRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

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
    public ResponseEntity<TenantConfig> getTenantConfig(
            @org.springframework.web.bind.annotation.PathVariable String tenantId) {
        return tenantConfigRepository.findById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @org.springframework.web.bind.annotation.PutMapping
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> updateTenantConfig(
            @org.springframework.web.bind.annotation.RequestBody TenantConfig config) {
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

    @org.springframework.web.bind.annotation.PostMapping("/{tenantId}/impersonate")
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> impersonateTenant(@org.springframework.web.bind.annotation.PathVariable String tenantId) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Error: Not authenticated");
            }

            // Extract the email from the principal (UserDetails)
            Object principal = authentication.getPrincipal();
            String currentUserEmail = null;

            if (principal instanceof UserDetails) {
                currentUserEmail = ((UserDetails) principal).getUsername();
            } else {
                currentUserEmail = authentication.getName();
            }

            if (currentUserEmail == null || currentUserEmail.isBlank()) {
                return ResponseEntity.status(401).body("Error: Could not extract user email from authentication");
            }

            // tenant-manager users exist in the "tenant" schema, not in the target tenant
            // schema
            // Save current context and switch to "tenant" schema to find the user
            String originalContext = TenantContext.getTenantId();
            try {
                TenantContext.setTenantId("tenant");

                var currentUser = userRepository.findByEmail(currentUserEmail)
                        .orElse(null);

                if (currentUser == null) {
                    return ResponseEntity.status(401).body("Error: User not found for email: " + currentUserEmail);
                }

                // Restore original context
                if (originalContext != null) {
                    TenantContext.setTenantId(originalContext);
                } else {
                    TenantContext.clear();
                }

                // Validate tenant exists
                if (!tenantConfigRepository.existsById(tenantId)) {
                    return ResponseEntity.status(404).body("Error: Tenant not found");
                }

                // Generate impersonation token with tenant context and original role
                String impersonationToken = jwtUtil.generateImpersonationToken(
                        currentUserEmail,
                        tenantId,
                        currentUser.getRole().getValue());

                // Return token in the format expected by Dart client
                Map<String, Object> response = new HashMap<>();
                response.put("token", impersonationToken);
                response.put("impersonationToken", impersonationToken);
                response.put("tenantId", tenantId);

                return ResponseEntity.ok(response);
            } finally {
                // Ensure context is properly restored
                if (originalContext != null) {
                    TenantContext.setTenantId(originalContext);
                } else {
                    TenantContext.clear();
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
