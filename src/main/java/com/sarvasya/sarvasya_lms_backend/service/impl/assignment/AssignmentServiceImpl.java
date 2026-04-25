package com.sarvasya.sarvasya_lms_backend.service.impl.assignment;

import com.sarvasya.sarvasya_lms_backend.model.assignment.Assignment;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.repository.assignment.AssignmentRepository;
import com.sarvasya.sarvasya_lms_backend.service.assignment.AssignmentService;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

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








