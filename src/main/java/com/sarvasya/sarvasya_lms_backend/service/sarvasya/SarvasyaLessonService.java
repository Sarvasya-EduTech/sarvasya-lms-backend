package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaLesson;

public interface SarvasyaLessonService {
    SarvasyaLesson create(SarvasyaLesson sarvasyaLesson);

    List<SarvasyaLesson> findAll();

    Optional<SarvasyaLesson> findById(UUID id);

    SarvasyaLesson update(UUID id, SarvasyaLesson updated);

    void delete(UUID id);

}








