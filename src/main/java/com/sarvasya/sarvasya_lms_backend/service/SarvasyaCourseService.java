package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaCourse;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaCourseService {

    private final SarvasyaCourseRepository repository;

    @Transactional
    public SarvasyaCourse create(SarvasyaCourse sarvasyaCourse) {
        return repository.save(sarvasyaCourse);
    }

    public List<SarvasyaCourse> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaCourse> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaCourse update(UUID id, SarvasyaCourse updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaCourse not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
