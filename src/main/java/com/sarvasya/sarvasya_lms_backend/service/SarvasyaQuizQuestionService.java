package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaQuizQuestion;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaQuizQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaQuizQuestionService {

    private final SarvasyaQuizQuestionRepository repository;

    @Transactional
    public SarvasyaQuizQuestion create(SarvasyaQuizQuestion sarvasyaQuizQuestion) {
        return repository.save(sarvasyaQuizQuestion);
    }

    public List<SarvasyaQuizQuestion> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaQuizQuestion> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaQuizQuestion update(UUID id, SarvasyaQuizQuestion updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaQuizQuestion not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
