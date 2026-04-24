package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaExam;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaExamRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaExamService;

@Service
@RequiredArgsConstructor
public class SarvasyaExamServiceImpl implements SarvasyaExamService {

    private final SarvasyaExamRepository repository;

    @Transactional
    public SarvasyaExam create(SarvasyaExam sarvasyaExam) {
        return repository.save(sarvasyaExam);
    }

    public List<SarvasyaExam> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaExam> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaExam update(UUID id, SarvasyaExam updated) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDescription(updated.getDescription());
            existing.setDurationMinutes(updated.getDurationMinutes());
            existing.setPassingScore(updated.getPassingScore());
            existing.setCourseId(updated.getCourseId());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaExam not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








