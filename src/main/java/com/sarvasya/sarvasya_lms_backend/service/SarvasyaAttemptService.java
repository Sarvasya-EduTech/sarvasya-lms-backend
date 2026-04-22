package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.AssessmentType;
import com.sarvasya.sarvasya_lms_backend.model.AttemptStatus;
import com.sarvasya.sarvasya_lms_backend.model.SarvasyaAttempt;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaAttemptService {

    private final SarvasyaAttemptRepository repository;

    @Transactional
    public SarvasyaAttempt create(SarvasyaAttempt sarvasyaAttempt) {
        return repository.save(sarvasyaAttempt);
    }

    @Transactional
    public SarvasyaAttempt startOrResume(UUID studentId, UUID assessmentId, AssessmentType type) {
        return repository
                .findTopByStudentIdAndAssessmentIdAndTypeAndStatusOrderByUpdatedAtDesc(
                        studentId,
                        assessmentId,
                        type,
                        AttemptStatus.IN_PROGRESS
                )
                .orElseGet(() -> repository.save(
                        SarvasyaAttempt.builder()
                                .studentId(studentId)
                                .assessmentId(assessmentId)
                                .type(type)
                                .status(AttemptStatus.IN_PROGRESS)
                                .isPassed(false)
                                .build()
                ));
    }

    public Optional<SarvasyaAttempt> findLatest(UUID studentId, UUID assessmentId, AssessmentType type) {
        final List<SarvasyaAttempt> list = repository.findLatestAttempts(studentId, assessmentId, type);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<SarvasyaAttempt> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaAttempt> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaAttempt update(UUID id, SarvasyaAttempt updated) {
        return repository.findById(id).map(existing -> {
            if (updated.getStudentId() != null) existing.setStudentId(updated.getStudentId());
            if (updated.getAssessmentId() != null) existing.setAssessmentId(updated.getAssessmentId());
            if (updated.getType() != null) existing.setType(updated.getType());
            if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
            if (updated.getScore() != null) existing.setScore(updated.getScore());
            if (updated.getMaxScore() != null) existing.setMaxScore(updated.getMaxScore());
            if (updated.getPercentage() != null) existing.setPercentage(updated.getPercentage());
            if (updated.getIsPassed() != null) existing.setIsPassed(updated.getIsPassed());
            if (updated.getTotalQuestions() != null) existing.setTotalQuestions(updated.getTotalQuestions());
            if (updated.getCorrectAnswers() != null) existing.setCorrectAnswers(updated.getCorrectAnswers());
            if (updated.getSubmittedAt() != null) existing.setSubmittedAt(updated.getSubmittedAt());
            if (updated.getStartedAt() != null) existing.setStartedAt(updated.getStartedAt());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaAttempt not found"));
    }

    @Transactional
    public SarvasyaAttempt submit(UUID id, SarvasyaAttempt updated) {
        return repository.findById(id).map(existing -> {
            existing.setStatus(AttemptStatus.SUBMITTED);
            existing.setScore(updated.getScore());
            existing.setMaxScore(updated.getMaxScore());
            existing.setPercentage(updated.getPercentage());
            existing.setIsPassed(updated.getIsPassed());
            existing.setTotalQuestions(updated.getTotalQuestions());
            existing.setCorrectAnswers(updated.getCorrectAnswers());
            existing.setSubmittedAt(updated.getSubmittedAt() != null ? updated.getSubmittedAt() : LocalDateTime.now());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaAttempt not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
