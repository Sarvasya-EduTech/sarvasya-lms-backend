package com.sarvasya.sarvasya_lms_backend.repository.sarvasya;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaCertificate;

@Repository
public interface SarvasyaCertificateRepository extends JpaRepository<SarvasyaCertificate, UUID> {
    Optional<SarvasyaCertificate> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
}








