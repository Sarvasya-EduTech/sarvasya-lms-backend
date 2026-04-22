package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaStudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SarvasyaStudentAnswerRepository extends JpaRepository<SarvasyaStudentAnswer, UUID> {
    List<SarvasyaStudentAnswer> findByAttemptId(UUID attemptId);
    Optional<SarvasyaStudentAnswer> findByAttemptIdAndQuestionId(UUID attemptId, UUID questionId);
}
