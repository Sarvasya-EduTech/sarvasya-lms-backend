package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaLesson;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaLessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaLessonService {

    private final SarvasyaLessonRepository repository;

    @Transactional
    public SarvasyaLesson create(SarvasyaLesson sarvasyaLesson) {
        return repository.save(sarvasyaLesson);
    }

    public List<SarvasyaLesson> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaLesson> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaLesson update(UUID id, SarvasyaLesson updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaLesson not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
