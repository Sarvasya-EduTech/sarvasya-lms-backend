package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaLesson;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaLessonService;

@RestController
@RequestMapping("/sarvasya/lessons")
@RequiredArgsConstructor
public class SarvasyaLessonController {

    private final SarvasyaLessonService service;

    @PostMapping
    public ResponseEntity<SarvasyaLesson> create(@RequestBody SarvasyaLesson payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaLesson>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaLesson> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaLesson> update(@PathVariable UUID id, @RequestBody SarvasyaLesson payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








