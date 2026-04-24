package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuiz;

public interface SarvasyaQuizService {
    SarvasyaQuiz create(SarvasyaQuiz sarvasyaQuiz);

    List<SarvasyaQuiz> findAll();

    Optional<SarvasyaQuiz> findById(UUID id);

    SarvasyaQuiz update(UUID id, SarvasyaQuiz updated);

    void delete(UUID id);

}








