package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaCourse;

public interface SarvasyaCourseService {

    SarvasyaCourse create(SarvasyaCourse sarvasyaCourse);

    List<SarvasyaCourse> findAll();

    Optional<SarvasyaCourse> findById(UUID id);

    SarvasyaCourse update(UUID id, SarvasyaCourse updated);

    void delete(UUID id);
}








