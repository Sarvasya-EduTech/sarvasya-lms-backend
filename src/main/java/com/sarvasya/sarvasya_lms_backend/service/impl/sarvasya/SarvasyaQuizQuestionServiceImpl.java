package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaQuizQuestion;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaQuizQuestionRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaQuizQuestionService;

@Service
@RequiredArgsConstructor
public class SarvasyaQuizQuestionServiceImpl implements SarvasyaQuizQuestionService {

    private final SarvasyaQuizQuestionRepository repository;

    @Transactional
    public SarvasyaQuizQuestion create(SarvasyaQuizQuestion sarvasyaQuizQuestion) {
        return repository.save(sarvasyaQuizQuestion);
    }

    public List<SarvasyaQuizQuestion> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaQuizQuestion> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaQuizQuestion update(UUID id, SarvasyaQuizQuestion updated) {
        return repository.findById(id).map(existing -> {
            existing.setQuizId(updated.getQuizId());
            existing.setQuestionId(updated.getQuestionId());
            existing.setOrderIndex(updated.getOrderIndex());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaQuizQuestion not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}









