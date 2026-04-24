package com.sarvasya.sarvasya_lms_backend.service.degree;

import com.sarvasya.sarvasya_lms_backend.model.degree.Degree;
import java.util.List;
import java.util.UUID;

public interface DegreeService {
    List<Degree> findAll();

    Degree save(Degree degree);

    java.util.Optional<Degree> findById(UUID id);

    void delete(UUID id);

}








