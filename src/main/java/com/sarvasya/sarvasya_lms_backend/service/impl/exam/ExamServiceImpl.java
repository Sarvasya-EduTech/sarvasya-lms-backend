package com.sarvasya.sarvasya_lms_backend.service.impl.exam;

import com.sarvasya.sarvasya_lms_backend.model.exam.Exam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.repository.exam.ExamRepository;
import com.sarvasya.sarvasya_lms_backend.service.exam.ExamService;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

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








