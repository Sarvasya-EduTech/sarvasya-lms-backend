package com.sarvasya.sarvasya_lms_backend.service.classes;

import com.sarvasya.sarvasya_lms_backend.model.classes.Classes;
import java.util.List;
import java.util.UUID;

public interface ClassesService {
    List<Classes> findAll();

    Classes save(Classes item);

    void delete(UUID id);

    Classes findById(UUID id);

}








