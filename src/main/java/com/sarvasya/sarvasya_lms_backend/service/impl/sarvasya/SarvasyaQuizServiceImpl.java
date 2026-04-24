package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuiz;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaQuizRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaQuizService;

@Service
@RequiredArgsConstructor
public class SarvasyaQuizServiceImpl implements SarvasyaQuizService {

    private final SarvasyaQuizRepository repository;

    @Transactional
    public SarvasyaQuiz create(SarvasyaQuiz sarvasyaQuiz) {
        return repository.save(sarvasyaQuiz);
    }

    public List<SarvasyaQuiz> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaQuiz> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaQuiz update(UUID id, SarvasyaQuiz updated) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDescription(updated.getDescription());
            existing.setDurationMinutes(updated.getDurationMinutes());
            existing.setPassingScore(updated.getPassingScore());
            existing.setModuleId(updated.getModuleId());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaQuiz not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








