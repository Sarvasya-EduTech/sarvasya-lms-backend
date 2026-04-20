package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.Exam;
import com.sarvasya.sarvasya_lms_backend.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository repository;

    public List<Exam> findAll() {
        return repository.findAll();
    }

    public Exam save(Exam item) {
        return repository.save(item);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
