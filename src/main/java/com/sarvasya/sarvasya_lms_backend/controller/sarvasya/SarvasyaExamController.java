package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaExam;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaExamService;

@RestController
@RequestMapping("/sarvasya/exams")
@RequiredArgsConstructor
public class SarvasyaExamController {

    private final SarvasyaExamService service;

    @PostMapping
    public ResponseEntity<SarvasyaExam> create(@RequestBody SarvasyaExam payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaExam>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaExam> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaExam> update(@PathVariable UUID id, @RequestBody SarvasyaExam payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








