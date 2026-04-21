package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaStudentAnswer;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaStudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaStudentAnswerService {

    private final SarvasyaStudentAnswerRepository repository;

    @Transactional
    public SarvasyaStudentAnswer create(SarvasyaStudentAnswer sarvasyaStudentAnswer) {
        return repository.save(sarvasyaStudentAnswer);
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
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaStudentAnswer not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
