package com.sarvasya.sarvasya_lms_backend.repository.exam;

import com.sarvasya.sarvasya_lms_backend.model.exam.Exam;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
}








