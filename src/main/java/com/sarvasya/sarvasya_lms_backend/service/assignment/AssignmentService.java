package com.sarvasya.sarvasya_lms_backend.service.assignment;

import com.sarvasya.sarvasya_lms_backend.model.assignment.Assignment;
import java.util.List;
import java.util.UUID;

public interface AssignmentService {
    List<Assignment> findAll();

    Assignment save(Assignment item);

    void delete(UUID id);

}








