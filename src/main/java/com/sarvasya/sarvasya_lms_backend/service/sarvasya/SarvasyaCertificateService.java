package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaCertificate;

public interface SarvasyaCertificateService {
    SarvasyaCertificate create(SarvasyaCertificate sarvasyaCertificate);

    Optional<SarvasyaCertificate> findByStudentAndCourse(UUID studentId, UUID courseId);

    SarvasyaCertificate createOrUpdate(SarvasyaCertificate payload);

    List<SarvasyaCertificate> findAll();

    Optional<SarvasyaCertificate> findById(UUID id);

    SarvasyaCertificate update(UUID id, SarvasyaCertificate updated);

    void delete(UUID id);

}








