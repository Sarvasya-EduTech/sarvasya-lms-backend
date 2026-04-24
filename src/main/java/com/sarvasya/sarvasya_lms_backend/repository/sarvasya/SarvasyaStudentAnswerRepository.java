package com.sarvasya.sarvasya_lms_backend.repository.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaStudentAnswer;

@Repository
public interface SarvasyaStudentAnswerRepository extends JpaRepository<SarvasyaStudentAnswer, UUID> {
    List<SarvasyaStudentAnswer> findByAttemptId(UUID attemptId);
    Optional<SarvasyaStudentAnswer> findByAttemptIdAndQuestionId(UUID attemptId, UUID questionId);
}








