package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaEnrollment;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaEnrollmentService {

    private final SarvasyaEnrollmentRepository repository;

    @Transactional
    public SarvasyaEnrollment create(SarvasyaEnrollment sarvasyaEnrollment) {
        return repository.save(sarvasyaEnrollment);
    }

    public List<SarvasyaEnrollment> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaEnrollment> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaEnrollment update(UUID id, SarvasyaEnrollment updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaEnrollment not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
