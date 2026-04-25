package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaStudentAnswer;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaStudentAnswerRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaStudentAnswerService;

@Service
@RequiredArgsConstructor
public class SarvasyaStudentAnswerServiceImpl implements SarvasyaStudentAnswerService {

    private final SarvasyaStudentAnswerRepository repository;

    @Transactional
    public SarvasyaStudentAnswer create(SarvasyaStudentAnswer sarvasyaStudentAnswer) {
        return repository.save(sarvasyaStudentAnswer);
    }

    public List<SarvasyaStudentAnswer> findByAttemptId(UUID attemptId) {
        return repository.findByAttemptId(attemptId);
    }

    @Transactional
    public SarvasyaStudentAnswer upsert(SarvasyaStudentAnswer payload) {
        if (payload.getAttemptId() == null || payload.getQuestionId() == null) {
            throw new RuntimeException("attemptId and questionId are required");
        }
        return repository.findByAttemptIdAndQuestionId(payload.getAttemptId(), payload.getQuestionId())
                .map(existing -> {
                    if (payload.getSelectedOptionIds() != null) existing.setSelectedOptionIds(payload.getSelectedOptionIds());
                    if (payload.getNatAnswerGiven() != null) existing.setNatAnswerGiven(payload.getNatAnswerGiven());
                    if (payload.getIsCorrect() != null) existing.setIsCorrect(payload.getIsCorrect());
                    if (payload.getMarksAwarded() != null) existing.setMarksAwarded(payload.getMarksAwarded());
                    return repository.save(existing);
                })
                .orElseGet(() -> repository.save(payload));
    }

    public List<SarvasyaStudentAnswer> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaStudentAnswer> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaStudentAnswer update(UUID id, SarvasyaStudentAnswer updated) {
        return repository.findById(id).map(existing -> {
            if (updated.getAttemptId() != null) existing.setAttemptId(updated.getAttemptId());
            if (updated.getQuestionId() != null) existing.setQuestionId(updated.getQuestionId());
            if (updated.getSelectedOptionIds() != null) existing.setSelectedOptionIds(updated.getSelectedOptionIds());
            if (updated.getNatAnswerGiven() != null) existing.setNatAnswerGiven(updated.getNatAnswerGiven());
            if (updated.getIsCorrect() != null) existing.setIsCorrect(updated.getIsCorrect());
            if (updated.getMarksAwarded() != null) existing.setMarksAwarded(updated.getMarksAwarded());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaStudentAnswer not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








