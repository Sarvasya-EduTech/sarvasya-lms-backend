package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.AttendanceBulkDeleteRequest;
import com.sarvasya.sarvasya_lms_backend.dto.ProfessorAssignmentRequest;
import com.sarvasya.sarvasya_lms_backend.model.ProfessorCourseClassAssignment;
import com.sarvasya.sarvasya_lms_backend.service.ProfessorAssignmentService;
import com.sarvasya.sarvasya_lms_backend.service.StudentIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{tenantName}/professor-assignments")
@RequiredArgsConstructor
public class ProfessorAssignmentController {
    private final ProfessorAssignmentService service;
    private final StudentIdResolver studentIdResolver;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<List<ProfessorCourseClassAssignment>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<List<ProfessorCourseClassAssignment>> getMyAssignments(
            @PathVariable("tenantName") String tenantName) {
        UUID professorId = studentIdResolver.resolveCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("Unable to resolve user"));
        return ResponseEntity.ok(service.getByProfessorId(professorId));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<List<ProfessorCourseClassAssignment>> createBulk(
            @PathVariable("tenantName") String tenantName,
            @RequestBody ProfessorAssignmentRequest request) {
        return ResponseEntity.ok(service.createBulk(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<?> delete(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-delete")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<?> bulkDelete(
            @PathVariable("tenantName") String tenantName,
            @RequestBody AttendanceBulkDeleteRequest request) {
        service.bulkDelete(request.getIds());
        return ResponseEntity.ok().build();
    }
}
