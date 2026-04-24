package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaModule;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaModuleService;

@RestController
@RequestMapping("/sarvasya/modules")
@RequiredArgsConstructor
public class SarvasyaModuleController {

    private final SarvasyaModuleService service;

    @PostMapping
    public ResponseEntity<SarvasyaModule> create(@RequestBody SarvasyaModule payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaModule>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaModule> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaModule> update(@PathVariable UUID id, @RequestBody SarvasyaModule payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








