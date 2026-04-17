package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.ThemeSettingsDto;
import com.sarvasya.sarvasya_lms_backend.service.ThemeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/theme")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeSettingsService themeSettingsService;

    @GetMapping
    public ResponseEntity<ThemeSettingsDto> getThemeSettings(@PathVariable String tenantId) {
        return ResponseEntity.ok(themeSettingsService.getThemeSettings());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<ThemeSettingsDto> updateThemeSettings(
            @PathVariable String tenantId,
            @RequestBody ThemeSettingsDto themeSettingsDto) {
        return ResponseEntity.ok(themeSettingsService.updateThemeSettings(themeSettingsDto));
    }
}
