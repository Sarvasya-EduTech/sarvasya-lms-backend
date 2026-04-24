package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaLesson;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaLessonRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaLessonService;

@Service
@RequiredArgsConstructor
public class SarvasyaLessonServiceImpl implements SarvasyaLessonService {

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
            existing.setTitle(updated.getTitle());
            existing.setVideoUrl(updated.getVideoUrl());
            existing.setOrderIndex(updated.getOrderIndex());
            existing.setIsPreview(updated.getIsPreview());
            existing.setModuleId(updated.getModuleId());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaLesson not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








