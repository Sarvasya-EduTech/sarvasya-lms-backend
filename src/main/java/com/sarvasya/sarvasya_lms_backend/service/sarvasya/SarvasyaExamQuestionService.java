package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaExamQuestion;

public interface SarvasyaExamQuestionService {
    SarvasyaExamQuestion create(SarvasyaExamQuestion sarvasyaExamQuestion);

    List<SarvasyaExamQuestion> findAll();

    Optional<SarvasyaExamQuestion> findById(UUID id);

    SarvasyaExamQuestion update(UUID id, SarvasyaExamQuestion updated);

    void delete(UUID id);

}








