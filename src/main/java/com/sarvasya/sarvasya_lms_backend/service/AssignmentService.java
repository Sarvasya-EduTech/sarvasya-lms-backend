package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.Assignment;
import com.sarvasya.sarvasya_lms_backend.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository repository;

    public List<Assignment> findAll() {
        return repository.findAll();
    }

    @Transactional
    public Assignment save(Assignment item) {
        return repository.save(item);
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
