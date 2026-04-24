package com.sarvasya.sarvasya_lms_backend.controller.degree;

import com.sarvasya.sarvasya_lms_backend.model.degree.Degree;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.service.degree.DegreeService;

@RestController
@RequestMapping("/{tenantName}/degrees")
@RequiredArgsConstructor
public class DegreeController {

    private final DegreeService service;

    @GetMapping
    public ResponseEntity<List<Degree>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Degree> getById(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Degree> create(
            @PathVariable("tenantName") String tenantName,
            @RequestBody Degree degree) {
        return ResponseEntity.ok(service.save(degree));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Degree> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody Degree degree) {
        degree.setId(id);
        return ResponseEntity.ok(service.save(degree));
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








