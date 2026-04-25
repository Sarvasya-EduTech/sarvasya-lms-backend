package com.sarvasya.sarvasya_lms_backend.service.exam;

import com.sarvasya.sarvasya_lms_backend.model.exam.Exam;
import java.util.List;
import java.util.UUID;

public interface ExamService {
    List<Exam> findAll();

    Exam save(Exam item);

    void delete(UUID id);

}








