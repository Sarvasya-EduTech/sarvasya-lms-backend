package com.sarvasya.sarvasya_lms_backend.repository.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.common.AssessmentType;
import com.sarvasya.sarvasya_lms_backend.model.common.AttemptStatus;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaAttempt;

@Repository
public interface SarvasyaAttemptRepository extends JpaRepository<SarvasyaAttempt, UUID> {
    Optional<SarvasyaAttempt> findTopByStudentIdAndAssessmentIdAndTypeAndStatusOrderByUpdatedAtDesc(
            UUID studentId,
            UUID assessmentId,
            AssessmentType type,
            AttemptStatus status
    );

    @Query("""
            select a from SarvasyaAttempt a
            where a.studentId = :studentId
              and a.assessmentId = :assessmentId
              and a.type = :type
            order by coalesce(a.submittedAt, a.updatedAt) desc, a.updatedAt desc
            """)
    List<SarvasyaAttempt> findLatestAttempts(UUID studentId, UUID assessmentId, AssessmentType type);
}








