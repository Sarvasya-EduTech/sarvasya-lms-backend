package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.ThemeSettingsDto;
import com.sarvasya.sarvasya_lms_backend.service.ThemeSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeSettingsService themeSettingsService;
    private static final String UPLOAD_DIR = "uploads/logos";

    @GetMapping("/{tenantId}/theme")
    public ResponseEntity<ThemeSettingsDto> getThemeSettings(@PathVariable("tenantId") String tenantId) {
        return ResponseEntity.ok(themeSettingsService.getThemeSettings());
    }

    @PutMapping("/{tenantId}/theme")
    @PreAuthorize("hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> updateThemeSettingsLocal(
            @PathVariable("tenantId") String tenantId,
            @RequestBody ThemeSettingsDto themeSettingsDto) {
        themeSettingsService.updateThemeSettings(themeSettingsDto);
        return ResponseEntity.ok(java.util.Map.of("message", "Theme updated successfully"));
    }

    // Global Manager update (No tenantId in URL)
    @PutMapping("/theme")
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> updateThemeSettingsGlobal(@RequestBody ThemeSettingsDto dto) {
        String targetTenantId = dto.getTenantId();
        if (targetTenantId == null || targetTenantId.isBlank()) {
            return ResponseEntity.badRequest().body("Error: tenantId is required in the request body for global updates.");
        }

        String originalTenant = com.sarvasya.sarvasya_lms_backend.security.TenantContext.getTenantId();
        try {
            com.sarvasya.sarvasya_lms_backend.security.TenantContext.setTenantId(targetTenantId);
            themeSettingsService.updateThemeSettings(dto);
        } finally {
            com.sarvasya.sarvasya_lms_backend.security.TenantContext.setTenantId(originalTenant);
        }
        return ResponseEntity.ok(java.util.Map.of("message", "Global theme update successful for " + targetTenantId));
    }


    @PostMapping("/{tenantId}/theme/logo")
    @PreAuthorize("hasAuthority('tenant-manager') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> uploadLogo(
            @PathVariable("tenantId") String tenantId,
            @RequestParam("file") MultipartFile file) {
        System.out.println("Uploading logo for tenant: " + tenantId);
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("Current user: " + auth.getName());
            System.out.println("Authorities: " + auth.getAuthorities());
        }
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Create upload directory if it doesn't exist
            Path rootPath = Paths.get(UPLOAD_DIR);
            Path tenantPath = rootPath.resolve(tenantId);
            if (!Files.exists(tenantPath)) {
                Files.createDirectories(tenantPath);
            }

            // Save file
            String fileName = "logo_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = tenantPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update database with URL (relative path for serving)
            String logoUrl = "/api/v1/tenants/" + tenantId + "/theme/logo/" + fileName;
            themeSettingsService.saveLogo(logoUrl);

            return ResponseEntity.ok(java.util.Map.of(
                "message", "Logo uploaded successfully",
                "logoUrl", logoUrl
            ));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Could not upload the file: " + e.getMessage());
        }
    }

    @GetMapping("/{tenantId}/theme/logo/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> getLogo(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("fileName") String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(tenantId).resolve(fileName);
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
    @DeleteMapping("/{tenantId}/theme/logo")
    public ResponseEntity<?> deleteLogo(@PathVariable("tenantId") String tenantId) {
        System.out.println(">>> DELETE LOGO REQUEST for tenant: " + tenantId);
        
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            System.out.println(">>> Unauthorized: No authentication found");
            return ResponseEntity.status(401).body("Authentication required");
        }

        System.out.println(">>> User: " + auth.getName());
        System.out.println(">>> Authorities: " + auth.getAuthorities());

        boolean isSarvasyaAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("sarvasya-admin") || 
                               a.getAuthority().equalsIgnoreCase("ROLE_sarvasya-admin") ||
                               a.getAuthority().equalsIgnoreCase("SARVASYA_ADMIN"));

        if (!isSarvasyaAdmin) {
            System.out.println(">>> Forbidden: User does not have sarvasya-admin authority");
            return ResponseEntity.status(403).body("Only sarvasya-admin can remove the logo. Your authorities: " + auth.getAuthorities());
        }

        try {
            themeSettingsService.deleteLogo();
            System.out.println(">>> Logo deleted successfully for " + tenantId);
            return ResponseEntity.ok(java.util.Map.of("message", "Logo removed successfully"));
        } catch (Exception e) {
            System.err.println(">>> Deletion error: " + e.getMessage());
            return ResponseEntity.status(500).body("Error removing logo: " + e.getMessage());
        }
    }
}
