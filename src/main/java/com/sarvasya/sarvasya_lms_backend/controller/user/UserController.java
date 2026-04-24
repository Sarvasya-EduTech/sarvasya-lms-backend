package com.sarvasya.sarvasya_lms_backend.controller.user;

import com.sarvasya.sarvasya_lms_backend.model.user.User;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sarvasya.sarvasya_lms_backend.dto.user.BulkUserDeleteRequest;
import com.sarvasya.sarvasya_lms_backend.dto.user.UserAssignClassRequest;
import com.sarvasya.sarvasya_lms_backend.dto.user.UserAssignDegreeRequest;
import com.sarvasya.sarvasya_lms_backend.dto.user.UserCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.user.UserProfileResponse;
import com.sarvasya.sarvasya_lms_backend.dto.user.UserSummaryResponse;
import com.sarvasya.sarvasya_lms_backend.model.common.Role;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import com.sarvasya.sarvasya_lms_backend.service.user.UserService;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private Role getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().isEmpty()) {
            throw new IllegalArgumentException("No authentication found");
        }
        return Role.fromValue(auth.getAuthorities().iterator().next().getAuthority());
    }

    @PostMapping("/{tenantName}/users")
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('admin') or hasAuthority('professor')")
    public ResponseEntity<?> createUserLocal(
            @PathVariable String tenantName,
            @Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request, getCurrentUserRole());
        return ResponseEntity.ok(java.util.Map.of("message", "User created successfully"));
    }

    @PostMapping("/sarvasya/users")
    @PreAuthorize("hasAuthority('tenant-manager')")
    public ResponseEntity<?> createUserGlobal(@Valid @RequestBody UserCreateRequest request) {
        String targetTenantId = request.getTenantId();
        if (targetTenantId == null || targetTenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId is required in the request body for global user creation.");
        }

        String originalTenant = com.sarvasya.sarvasya_lms_backend.security.TenantContext.getTenantId();
        try {
            com.sarvasya.sarvasya_lms_backend.security.TenantContext.setTenantId(targetTenantId);
            userService.createUser(request, getCurrentUserRole());
            return ResponseEntity
                    .ok(java.util.Map.of("message", "User created successfully in tenant: " + targetTenantId));
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
            throw new IllegalArgumentException("Error reading file: " + e.getMessage());
        }
    }

    @PostMapping("/{tenantName}/users/bulk/json")
    @PreAuthorize("hasAuthority('professor') or hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> bulkCreateUsersJson(@PathVariable String tenantName,
            @Valid @RequestBody List<UserCreateRequest> requests) {
        userService.bulkCreateUsers(requests, getCurrentUserRole());
        return ResponseEntity.ok("Bulk users processed successfully from JSON");
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
        userService.bulkDeleteUsers(request.getIds(), getCurrentUserRole());
        return ResponseEntity.ok("Users deleted successfully");
    }

    @GetMapping("/{tenantName}/users/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String tenantName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalArgumentException("No authentication found");
        }
        return ResponseEntity.ok(userService.getCurrentUserProfile(auth.getName()));
    }

    @GetMapping("/{tenantName}/users")
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('admin') or hasAuthority('professor')")
    public ResponseEntity<List<UserSummaryResponse>> listUsers(@PathVariable String tenantName) {
        return ResponseEntity.ok(userService.listUsers());
    }

    @PatchMapping("/{tenantName}/users/{id}/degree")
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('admin')")
    public ResponseEntity<UserSummaryResponse> assignDegree(
            @PathVariable String tenantName,
            @PathVariable UUID id,
            @RequestBody UserAssignDegreeRequest body) {
        return ResponseEntity.ok(userService.assignDegree(id, body));
    }

    @GetMapping("/{tenantName}/users/class/{classId}")
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('admin') or hasAuthority('professor')")
    public ResponseEntity<List<UserSummaryResponse>> listUsersByClass(
            @PathVariable String tenantName,
            @PathVariable UUID classId) {
        return ResponseEntity.ok(userService.listUsersByClass(classId));
    }

    @PatchMapping("/{tenantName}/users/{id}/class")
    @PreAuthorize("hasAuthority('sarvasya-admin') or hasAuthority('admin') or hasAuthority('professor')")
    public ResponseEntity<UserSummaryResponse> assignClass(
            @PathVariable String tenantName,
            @PathVariable UUID id,
            @RequestBody UserAssignClassRequest body) {
        return ResponseEntity.ok(userService.assignClass(id, body));
    }
}








