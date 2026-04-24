package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaStudyMaterial;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaStudyMaterialService;

@RestController
@RequestMapping("/sarvasya/studymaterials")
@RequiredArgsConstructor
public class SarvasyaStudyMaterialController {

    private final SarvasyaStudyMaterialService service;

    @PostMapping
    public ResponseEntity<SarvasyaStudyMaterial> create(@RequestBody SarvasyaStudyMaterial payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaStudyMaterial>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaStudyMaterial> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaStudyMaterial> update(@PathVariable UUID id, @RequestBody SarvasyaStudyMaterial payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








