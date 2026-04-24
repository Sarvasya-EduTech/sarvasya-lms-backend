package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaCertificate;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaCertificateRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaCertificateService;

@Service
@RequiredArgsConstructor
public class SarvasyaCertificateServiceImpl implements SarvasyaCertificateService {

    private final SarvasyaCertificateRepository repository;

    @Transactional
    public SarvasyaCertificate create(SarvasyaCertificate sarvasyaCertificate) {
        return repository.save(sarvasyaCertificate);
    }

    public Optional<SarvasyaCertificate> findByStudentAndCourse(UUID studentId, UUID courseId) {
        return repository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Transactional
    public SarvasyaCertificate createOrUpdate(SarvasyaCertificate payload) {
        if (payload.getStudentId() == null || payload.getCourseId() == null) {
            throw new RuntimeException("studentId and courseId are required");
        }
        return repository.findByStudentIdAndCourseId(payload.getStudentId(), payload.getCourseId())
                .map(existing -> {
                    if (payload.getCertificateUrl() != null) existing.setCertificateUrl(payload.getCertificateUrl());
                    existing.setIssuedAt(payload.getIssuedAt() != null ? payload.getIssuedAt() : LocalDateTime.now());
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    if (payload.getIssuedAt() == null) payload.setIssuedAt(LocalDateTime.now());
                    return repository.save(payload);
                });
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
            if (updated.getStudentId() != null) existing.setStudentId(updated.getStudentId());
            if (updated.getCourseId() != null) existing.setCourseId(updated.getCourseId());
            if (updated.getCertificateUrl() != null) existing.setCertificateUrl(updated.getCertificateUrl());
            if (updated.getIssuedAt() != null) existing.setIssuedAt(updated.getIssuedAt());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaCertificate not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








