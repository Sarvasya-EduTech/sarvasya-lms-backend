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

import java.util.List;

@RestController
@RequestMapping("/api/{tenantName}/users")
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

    @PostMapping
    @PreAuthorize("hasAuthority('professor') or hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> createUser(@PathVariable String tenantName, @Valid @RequestBody UserCreateRequest request) {
        try {
            userService.createUser(request, getCurrentUserRole());
            return ResponseEntity.ok("User created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> bulkCreateUsers(@PathVariable String tenantName, @Valid @RequestBody List<UserCreateRequest> requests) {
        try {
            userService.bulkCreateUsers(requests, getCurrentUserRole());
            return ResponseEntity.ok("Bulk users processed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bulk/template")
    public ResponseEntity<byte[]> getBulkTemplate(@PathVariable String tenantName) {
        String csvContent = userService.getBulkUploadTemplate();
        byte[] csvBytes = csvContent.getBytes();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user_upload_template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    @DeleteMapping("/bulk")
    @PreAuthorize("hasAuthority('admin') or hasAuthority('sarvasya-admin')")
    public ResponseEntity<?> bulkDeleteUsers(@PathVariable String tenantName, @Valid @RequestBody BulkUserDeleteRequest request) {
        try {
            userService.bulkDeleteUsers(request.getIds());
            return ResponseEntity.ok("Users deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
