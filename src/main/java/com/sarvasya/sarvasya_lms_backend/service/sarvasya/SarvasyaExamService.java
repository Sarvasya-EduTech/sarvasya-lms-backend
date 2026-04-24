package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaExam;

public interface SarvasyaExamService {
    SarvasyaExam create(SarvasyaExam sarvasyaExam);

    List<SarvasyaExam> findAll();

    Optional<SarvasyaExam> findById(UUID id);

    SarvasyaExam update(UUID id, SarvasyaExam updated);

    void delete(UUID id);

}








