package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaStudentAnswer;

public interface SarvasyaStudentAnswerService {
    SarvasyaStudentAnswer create(SarvasyaStudentAnswer sarvasyaStudentAnswer);

    List<SarvasyaStudentAnswer> findByAttemptId(UUID attemptId);

    SarvasyaStudentAnswer upsert(SarvasyaStudentAnswer payload);

    List<SarvasyaStudentAnswer> findAll();

    Optional<SarvasyaStudentAnswer> findById(UUID id);

    SarvasyaStudentAnswer update(UUID id, SarvasyaStudentAnswer updated);

    void delete(UUID id);

}








