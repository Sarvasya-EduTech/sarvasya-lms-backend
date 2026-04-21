package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaEnrollment;
import com.sarvasya.sarvasya_lms_backend.service.SarvasyaEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sarvasya/{tenantid}/enrollments")
@RequiredArgsConstructor
public class SarvasyaEnrollmentController {

    private final SarvasyaEnrollmentService service;

    @PostMapping
    public ResponseEntity<SarvasyaEnrollment> create(@RequestBody SarvasyaEnrollment payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaEnrollment>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaEnrollment> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaEnrollment> update(@PathVariable UUID id, @RequestBody SarvasyaEnrollment payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
