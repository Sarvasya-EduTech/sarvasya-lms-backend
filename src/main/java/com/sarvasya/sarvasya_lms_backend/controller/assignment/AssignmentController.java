package com.sarvasya.sarvasya_lms_backend.controller.assignment;

import com.sarvasya.sarvasya_lms_backend.model.assignment.Assignment;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.service.assignment.AssignmentService;

@RestController
@RequestMapping("/{tenantName}/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService service;

    @GetMapping
    public ResponseEntity<List<Assignment>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<Assignment> create(
            @PathVariable("tenantName") String tenantName, 
            @RequestBody Assignment item) {
        return ResponseEntity.ok(service.save(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<Assignment> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody Assignment item) {
        item.setId(id);
        return ResponseEntity.ok(service.save(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<?> delete(
            @PathVariable("tenantName") String tenantName, 
            @PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








