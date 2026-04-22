package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.AssessmentType;
import com.sarvasya.sarvasya_lms_backend.model.SarvasyaAttempt;
import com.sarvasya.sarvasya_lms_backend.service.SarvasyaAttemptService;
import com.sarvasya.sarvasya_lms_backend.service.StudentIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sarvasya/{tenantid}/attempts")
@RequiredArgsConstructor
public class SarvasyaAttemptController {

    private final SarvasyaAttemptService service;
    private final StudentIdResolver studentIdResolver;

    @PostMapping
    public ResponseEntity<SarvasyaAttempt> create(@RequestBody Map<String, Object> payload) {
        final UUID studentId = studentIdResolver.resolveStudentId((String) payload.get("studentId"));
        final UUID assessmentId = UUID.fromString(String.valueOf(payload.get("assessmentId")));
        final AssessmentType type = AssessmentType.valueOf(String.valueOf(payload.get("type")));

        SarvasyaAttempt attempt = SarvasyaAttempt.builder()
                .studentId(studentId)
                .assessmentId(assessmentId)
                .type(type)
                .score(toBigDecimal(payload.get("score")))
                .maxScore(toBigDecimal(payload.get("maxScore")))
                .percentage(toBigDecimal(payload.get("percentage")))
                .isPassed(toBool(payload.get("isPassed")))
                .totalQuestions(toInt(payload.get("totalQuestions")))
                .correctAnswers(toInt(payload.get("correctAnswers")))
                .submittedAt(toDateTime(payload.get("submittedAt")))
                .build();

        return ResponseEntity.ok(service.create(attempt));
    }

    @PostMapping("/start")
    public ResponseEntity<SarvasyaAttempt> start(@RequestBody Map<String, Object> payload) {
        final String studentIdRaw = (String) payload.get("studentId");
        final Object assessmentIdRaw = payload.get("assessmentId");
        final Object typeRaw = payload.get("type");
        if (assessmentIdRaw == null || typeRaw == null) {
            return ResponseEntity.badRequest().build();
        }
        final UUID studentId = studentIdResolver.resolveStudentId(studentIdRaw);
        final UUID assessmentId = UUID.fromString(String.valueOf(assessmentIdRaw));
        final AssessmentType type = AssessmentType.valueOf(String.valueOf(typeRaw));
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
    public ResponseEntity<SarvasyaAttempt> submit(@PathVariable UUID id, @RequestBody Map<String, Object> payload) {
        SarvasyaAttempt updated = SarvasyaAttempt.builder()
                .score(toBigDecimal(payload.get("score")))
                .maxScore(toBigDecimal(payload.get("maxScore")))
                .percentage(toBigDecimal(payload.get("percentage")))
                .isPassed(toBool(payload.get("isPassed")))
                .totalQuestions(toInt(payload.get("totalQuestions")))
                .correctAnswers(toInt(payload.get("correctAnswers")))
                .submittedAt(toDateTime(payload.get("submittedAt")))
                .build();
        return ResponseEntity.ok(service.submit(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    private static BigDecimal toBigDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(v.toString()); } catch (Exception e) { return null; }
    }

    private static Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return null; }
    }

    private static Boolean toBool(Object v) {
        if (v == null) return null;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }

    private static LocalDateTime toDateTime(Object v) {
        if (v == null) return null;
        try { return LocalDateTime.parse(v.toString()); } catch (Exception e) { return null; }
    }
}
