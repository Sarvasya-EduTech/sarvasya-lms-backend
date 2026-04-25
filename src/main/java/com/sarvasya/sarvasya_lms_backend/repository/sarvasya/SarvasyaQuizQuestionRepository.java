package com.sarvasya.sarvasya_lms_backend.repository.sarvasya;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuizQuestion;

@Repository
public interface SarvasyaQuizQuestionRepository extends JpaRepository<SarvasyaQuizQuestion, UUID> {
}









