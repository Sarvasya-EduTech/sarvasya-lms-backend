package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaQuizQuestion;
import com.sarvasya.sarvasya_lms_backend.service.SarvasyaQuizQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sarvasya/quizquestions")
@RequiredArgsConstructor
public class SarvasyaQuizQuestionController {

    private final SarvasyaQuizQuestionService service;

    @PostMapping
    public ResponseEntity<SarvasyaQuizQuestion> create(@RequestBody SarvasyaQuizQuestion payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaQuizQuestion>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaQuizQuestion> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaQuizQuestion> update(@PathVariable UUID id, @RequestBody SarvasyaQuizQuestion payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
