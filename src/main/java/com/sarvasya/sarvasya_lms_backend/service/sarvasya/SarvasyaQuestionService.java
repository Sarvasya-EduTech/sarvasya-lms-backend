package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuestion;

public interface SarvasyaQuestionService {
    SarvasyaQuestion create(SarvasyaQuestion sarvasyaQuestion);

    List<SarvasyaQuestion> findAll();

    Optional<SarvasyaQuestion> findById(UUID id);

    SarvasyaQuestion update(UUID id, SarvasyaQuestion updated);

    void delete(UUID id);

}








