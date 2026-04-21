package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaExam;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaExamService {

    private final SarvasyaExamRepository repository;

    @Transactional
    public SarvasyaExam create(SarvasyaExam sarvasyaExam) {
        return repository.save(sarvasyaExam);
    }

    public List<SarvasyaExam> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaExam> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaExam update(UUID id, SarvasyaExam updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaExam not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
