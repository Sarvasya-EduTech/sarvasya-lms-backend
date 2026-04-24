package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaEnrollment;

public interface SarvasyaEnrollmentService {
    SarvasyaEnrollment create(SarvasyaEnrollment sarvasyaEnrollment);

    List<SarvasyaEnrollment> findAll();

    Optional<SarvasyaEnrollment> findById(UUID id);

    SarvasyaEnrollment update(UUID id, SarvasyaEnrollment updated);

    void delete(UUID id);

}








