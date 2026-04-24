package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuizQuestion;

public interface SarvasyaQuizQuestionService {
    SarvasyaQuizQuestion create(SarvasyaQuizQuestion sarvasyaQuizQuestion);

    List<SarvasyaQuizQuestion> findAll();

    Optional<SarvasyaQuizQuestion> findById(UUID id);

    SarvasyaQuizQuestion update(UUID id, SarvasyaQuizQuestion updated);

    void delete(UUID id);

}









