package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.TenantLimits;
import com.sarvasya.sarvasya_lms_backend.service.TenantLimitsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenantName}/limits")
@RequiredArgsConstructor
public class TenantLimitsController {

    private final TenantLimitsService tenantLimitsService;

    @GetMapping
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('admin')")
    public ResponseEntity<TenantLimits> getLimits(@PathVariable String tenantName) {
        return ResponseEntity.ok(tenantLimitsService.getLimits());
    }
}
