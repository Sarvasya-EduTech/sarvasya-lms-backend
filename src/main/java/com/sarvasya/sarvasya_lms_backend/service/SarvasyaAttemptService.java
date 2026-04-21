package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaAttempt;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<SarvasyaAttempt> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaAttempt> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaAttempt update(UUID id, SarvasyaAttempt updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaAttempt not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
