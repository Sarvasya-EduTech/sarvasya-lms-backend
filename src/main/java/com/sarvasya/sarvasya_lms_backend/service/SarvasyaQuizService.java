package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaQuiz;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaQuizService {

    private final SarvasyaQuizRepository repository;

    @Transactional
    public SarvasyaQuiz create(SarvasyaQuiz sarvasyaQuiz) {
        return repository.save(sarvasyaQuiz);
    }

    public List<SarvasyaQuiz> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaQuiz> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaQuiz update(UUID id, SarvasyaQuiz updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaQuiz not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
