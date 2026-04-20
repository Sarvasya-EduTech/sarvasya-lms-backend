package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.Degree;
import com.sarvasya.sarvasya_lms_backend.repository.DegreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DegreeService {

    private final DegreeRepository repository;

    public List<Degree> findAll() {
        return repository.findAll();
    }

    public Degree save(Degree degree) {
        return repository.save(degree);
    }

    public java.util.Optional<Degree> findById(UUID id) {
        return repository.findById(id);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
