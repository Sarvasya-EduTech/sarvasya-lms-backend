package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaOption;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaOptionService;

@RestController
@RequestMapping("/sarvasya/options")
@RequiredArgsConstructor
public class SarvasyaOptionController {

    private final SarvasyaOptionService service;

    @PostMapping
    public ResponseEntity<SarvasyaOption> create(@RequestBody SarvasyaOption payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaOption>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaOption> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaOption> update(@PathVariable UUID id, @RequestBody SarvasyaOption payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








