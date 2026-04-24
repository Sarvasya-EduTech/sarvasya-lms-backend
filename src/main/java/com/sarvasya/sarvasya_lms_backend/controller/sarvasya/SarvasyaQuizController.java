package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuiz;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaQuizService;

@RestController
@RequestMapping("/sarvasya/quizs")
@RequiredArgsConstructor
public class SarvasyaQuizController {

    private final SarvasyaQuizService service;

    @PostMapping
    public ResponseEntity<SarvasyaQuiz> create(@RequestBody SarvasyaQuiz payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaQuiz>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaQuiz> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaQuiz> update(@PathVariable UUID id, @RequestBody SarvasyaQuiz payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








