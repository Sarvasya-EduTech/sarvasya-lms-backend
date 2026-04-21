package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaCertificate;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaCertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaCertificateService {

    private final SarvasyaCertificateRepository repository;

    @Transactional
    public SarvasyaCertificate create(SarvasyaCertificate sarvasyaCertificate) {
        return repository.save(sarvasyaCertificate);
    }

    public List<SarvasyaCertificate> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaCertificate> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaCertificate update(UUID id, SarvasyaCertificate updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaCertificate not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
