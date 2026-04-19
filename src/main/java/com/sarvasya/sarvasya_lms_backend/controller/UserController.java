package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.BulkUserDeleteRequest;
import com.sarvasya.sarvasya_lms_backend.dto.UserCreateRequest;
import com.sarvasya.sarvasya_lms_backend.model.Role;
import com.sarvasya.sarvasya_lms_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private Role getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().isEmpty()) {
            throw new SecurityException("No authentication found");
        }
        return Role.fromValue(auth.getAuthorities().iterator().next().getAuthority());
    }

    @PostMapping("/{tenantName}/users")
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('admin') or hasAuthority('professor')")
    public ResponseEntity<?> createUserLocal(@PathVariable String tenantName,
            @Valid @RequestBody UserCreateRequest request) {
        try {
            userService.createUser(request, getCurrentUserRole());
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/v1/users")
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> createUserGlobal(@Valid @RequestBody UserCreateRequest request) {
        String targetTenantId = request.getTenantId();
        if (targetTenantId == null || targetTenantId.isBlank()) {
            return ResponseEntity.badRequest().body("Error: tenantId is required in the request body for global user creation.");
        }

        String originalTenant = com.sarvasya.sarvasya_lms_backend.security.TenantContext.getTenantId();
        try {
            com.sarvasya.sarvasya_lms_backend.security.TenantContext.setTenantId(targetTenantId);
            userService.createUser(request, getCurrentUserRole());
            return ResponseEntity.ok("User created successfully in tenant: " + targetTenantId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            com.sarvasya.sarvasya_lms_backend.security.TenantContext.setTenantId(originalTenant);
        }
    }

    @PostMapping("/{tenantName}/users/bulk")
    @PreAuthorize("hasAuthority('professor') or hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> bulkCreateUsers(@PathVariable String tenantName,
            @RequestParam("file") MultipartFile file) {
        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            userService.processBulkCsv(content, getCurrentUserRole());
            return ResponseEntity.ok("Bulk users processed successfully from CSV");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{tenantName}/users/bulk/json")
    @PreAuthorize("hasAuthority('professor') or hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> bulkCreateUsersJson(@PathVariable String tenantName,
            @Valid @RequestBody List<UserCreateRequest> requests) {
        try {
            userService.bulkCreateUsers(requests, getCurrentUserRole());
            return ResponseEntity.ok("Bulk users processed successfully from JSON");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{tenantName}/users/bulk/template")
    public ResponseEntity<byte[]> getBulkTemplate(@PathVariable String tenantName) {
        String csvContent = userService.getBulkUploadTemplate(getCurrentUserRole());
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_upload_template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    @DeleteMapping("/{tenantName}/users/bulk")
    @PreAuthorize("hasAuthority('tenant-manager') or hasAuthority('professor') or hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> bulkDeleteUsers(@PathVariable String tenantName,
            @Valid @RequestBody BulkUserDeleteRequest request) {
        try {
            userService.bulkDeleteUsers(request.getIds(), getCurrentUserRole());
            return ResponseEntity.ok("Users deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
