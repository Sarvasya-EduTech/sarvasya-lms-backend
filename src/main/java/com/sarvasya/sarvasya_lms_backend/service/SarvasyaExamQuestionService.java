package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaExamQuestion;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaExamQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaExamQuestionService {

    private final SarvasyaExamQuestionRepository repository;

    @Transactional
    public SarvasyaExamQuestion create(SarvasyaExamQuestion sarvasyaExamQuestion) {
        return repository.save(sarvasyaExamQuestion);
    }

    public List<SarvasyaExamQuestion> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaExamQuestion> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaExamQuestion update(UUID id, SarvasyaExamQuestion updated) {
        return repository.findById(id).map(existing -> {
            existing.setExamId(updated.getExamId());
            existing.setQuestionId(updated.getQuestionId());
            existing.setOrderIndex(updated.getOrderIndex());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaExamQuestion not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
