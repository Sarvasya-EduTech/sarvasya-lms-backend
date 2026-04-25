package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.attempt.AttemptCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.attempt.AttemptStartRequest;
import com.sarvasya.sarvasya_lms_backend.dto.sarvasya.attempt.AttemptSubmitRequest;
import com.sarvasya.sarvasya_lms_backend.model.common.AssessmentType;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaAttempt;
import com.sarvasya.sarvasya_lms_backend.service.common.StudentIdResolver;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaAttemptService;

@RestController
@RequestMapping("/sarvasya/{tenantName}/attempts")
@RequiredArgsConstructor
public class SarvasyaAttemptController {

    private final SarvasyaAttemptService service;
    private final StudentIdResolver studentIdResolver;

    @PostMapping
    public ResponseEntity<SarvasyaAttempt> create(@Valid @RequestBody AttemptCreateRequest payload) {
        final UUID studentId = studentIdResolver.resolveStudentId(payload.studentId());

        SarvasyaAttempt attempt = SarvasyaAttempt.builder()
                .studentId(studentId)
                .assessmentId(payload.assessmentId())
                .type(payload.type())
                .score(payload.score())
                .maxScore(payload.maxScore())
                .percentage(payload.percentage())
                .isPassed(payload.isPassed())
                .totalQuestions(payload.totalQuestions())
                .correctAnswers(payload.correctAnswers())
                .submittedAt(payload.submittedAt())
                .build();

        return ResponseEntity.ok(service.create(attempt));
    }

    @PostMapping("/start")
    public ResponseEntity<SarvasyaAttempt> start(@Valid @RequestBody AttemptStartRequest payload) {
        final UUID studentId = studentIdResolver.resolveStudentId(payload.studentId());
        final UUID assessmentId = payload.assessmentId();
        final AssessmentType type = payload.type();
        return ResponseEntity.ok(service.startOrResume(studentId, assessmentId, type));
    }

    @GetMapping("/latest")
    public ResponseEntity<SarvasyaAttempt> latest(
            @RequestParam(required = false) String studentId,
            @RequestParam String assessmentId,
            @RequestParam AssessmentType type
    ) {
        final UUID resolvedStudentId = studentIdResolver.resolveStudentId(studentId);
        final UUID resolvedAssessmentId = UUID.fromString(assessmentId);
        final Optional<SarvasyaAttempt> latest = service.findLatest(resolvedStudentId, resolvedAssessmentId, type);
        return latest.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaAttempt>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaAttempt> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaAttempt> update(@PathVariable UUID id, @RequestBody SarvasyaAttempt payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<SarvasyaAttempt> submit(@PathVariable UUID id, @RequestBody AttemptSubmitRequest payload) {
        SarvasyaAttempt updated = SarvasyaAttempt.builder()
                .score(payload.score())
                .maxScore(payload.maxScore())
                .percentage(payload.percentage())
                .isPassed(payload.isPassed())
                .totalQuestions(payload.totalQuestions())
                .correctAnswers(payload.correctAnswers())
                .submittedAt(payload.submittedAt())
                .build();
        return ResponseEntity.ok(service.submit(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








