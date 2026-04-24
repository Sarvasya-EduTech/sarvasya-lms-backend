package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.common.AssessmentType;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaAttempt;

public interface SarvasyaAttemptService {

    SarvasyaAttempt create(SarvasyaAttempt sarvasyaAttempt);

    SarvasyaAttempt startOrResume(UUID studentId, UUID assessmentId, AssessmentType type);

    Optional<SarvasyaAttempt> findLatest(UUID studentId, UUID assessmentId, AssessmentType type);

    List<SarvasyaAttempt> findAll();

    Optional<SarvasyaAttempt> findById(UUID id);

    SarvasyaAttempt update(UUID id, SarvasyaAttempt updated);

    SarvasyaAttempt submit(UUID id, SarvasyaAttempt updated);

    void delete(UUID id);
}








