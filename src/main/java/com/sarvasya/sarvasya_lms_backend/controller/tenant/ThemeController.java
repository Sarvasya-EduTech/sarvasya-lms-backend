package com.sarvasya.sarvasya_lms_backend.controller.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sarvasya.sarvasya_lms_backend.dto.common.LogoUploadResponse;
import com.sarvasya.sarvasya_lms_backend.dto.common.MessageResponse;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.ThemeSettingsDto;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import com.sarvasya.sarvasya_lms_backend.service.tenant.ThemeSettingsService;

@RestController
@RequestMapping("/sarvasya/tenants")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeSettingsService themeSettingsService;

    @GetMapping("/{tenantId}/theme")
    public ResponseEntity<ThemeSettingsDto> getThemeSettings(@PathVariable("tenantId") String tenantId) {
        return ResponseEntity.ok(themeSettingsService.getThemeSettings());
    }

    @PutMapping("/{tenantId}/theme")
    @PreAuthorize("hasAuthority('sarvasya-admin')")
    public ResponseEntity<MessageResponse> updateThemeSettingsLocal(
            @PathVariable("tenantId") String tenantId,
            @RequestBody ThemeSettingsDto themeSettingsDto) {
        themeSettingsService.updateThemeSettings(themeSettingsDto);
        return ResponseEntity.ok(new MessageResponse("Theme updated successfully"));
    }

    // Global Manager update (No tenantId in URL)
    @PutMapping("/theme")
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<MessageResponse> updateThemeSettingsGlobal(@RequestBody ThemeSettingsDto dto) {
        String targetTenantId = dto.getTenantId();
        if (targetTenantId == null || targetTenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId is required in the request body for global updates.");
        }

        String originalTenant = com.sarvasya.sarvasya_lms_backend.security.TenantContext.getTenantId();
        try {
            com.sarvasya.sarvasya_lms_backend.security.TenantContext.setTenantId(targetTenantId);
            themeSettingsService.updateThemeSettings(dto);
        } finally {
            com.sarvasya.sarvasya_lms_backend.security.TenantContext.setTenantId(originalTenant);
        }
        return ResponseEntity.ok(new MessageResponse("Global theme update successful for " + targetTenantId));
    }


    @PostMapping("/{tenantId}/theme/logo")
    @PreAuthorize("hasAuthority('tenant-manager') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<LogoUploadResponse> uploadLogo(
            @PathVariable("tenantId") String tenantId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(themeSettingsService.uploadLogo(tenantId, file));
    }

    @GetMapping("/{tenantId}/theme/logo/{fileName:.+}")
    public ResponseEntity<Resource> getLogo(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("fileName") String fileName) {
        Resource resource = themeSettingsService.getLogo(tenantId, fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @DeleteMapping("/{tenantId}/theme/logo")
    @PreAuthorize("hasAuthority('sarvasya-admin')")
    public ResponseEntity<MessageResponse> deleteLogo(@PathVariable("tenantId") String tenantId) {
        themeSettingsService.deleteLogo();
        return ResponseEntity.ok(new MessageResponse("Logo removed successfully"));
    }
}








