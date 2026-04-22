package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SarvasyaCertificateRepository extends JpaRepository<SarvasyaCertificate, UUID> {
    Optional<SarvasyaCertificate> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
}
