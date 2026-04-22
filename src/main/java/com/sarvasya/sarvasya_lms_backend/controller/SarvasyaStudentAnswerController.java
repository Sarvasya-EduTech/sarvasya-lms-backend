package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaStudentAnswer;
import com.sarvasya.sarvasya_lms_backend.service.SarvasyaStudentAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sarvasya/{tenantid}/studentanswers")
@RequiredArgsConstructor
public class SarvasyaStudentAnswerController {

    private final SarvasyaStudentAnswerService service;

    @PostMapping
    public ResponseEntity<SarvasyaStudentAnswer> create(@RequestBody Map<String, Object> payload) {
        final SarvasyaStudentAnswer answer = SarvasyaStudentAnswer.builder()
                .attemptId(UUID.fromString(String.valueOf(payload.get("attemptId"))))
                .questionId(UUID.fromString(String.valueOf(payload.get("questionId"))))
                .selectedOptionIds(payload.get("selectedOptionIds") != null ? payload.get("selectedOptionIds").toString() : null)
                .build();
        return ResponseEntity.ok(service.create(answer));
    }

    @PutMapping("/upsert")
    public ResponseEntity<SarvasyaStudentAnswer> upsert(@RequestBody Map<String, Object> payload) {
        final SarvasyaStudentAnswer answer = SarvasyaStudentAnswer.builder()
                .attemptId(UUID.fromString(String.valueOf(payload.get("attemptId"))))
                .questionId(UUID.fromString(String.valueOf(payload.get("questionId"))))
                .selectedOptionIds(payload.get("selectedOptionIds") != null ? payload.get("selectedOptionIds").toString() : null)
                .build();
        return ResponseEntity.ok(service.upsert(answer));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaStudentAnswer>> findAll(@RequestParam(required = false) String attemptId) {
        if (attemptId != null && !attemptId.isBlank()) {
            return ResponseEntity.ok(service.findByAttemptId(UUID.fromString(attemptId)));
        }
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaStudentAnswer> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaStudentAnswer> update(@PathVariable UUID id, @RequestBody SarvasyaStudentAnswer payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
