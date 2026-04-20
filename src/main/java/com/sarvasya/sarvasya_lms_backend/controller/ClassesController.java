package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.Classes;
import com.sarvasya.sarvasya_lms_backend.service.ClassesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{tenantName}/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService service;

    @GetMapping
    public ResponseEntity<List<Classes>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Classes> create(
            @PathVariable("tenantName") String tenantName, 
            @RequestBody Classes item) {
        return ResponseEntity.ok(service.save(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<?> delete(
            @PathVariable("tenantName") String tenantName, 
            @PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
