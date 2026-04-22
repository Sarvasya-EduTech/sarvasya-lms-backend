package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaQuestion;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaQuestionService {

    private final SarvasyaQuestionRepository repository;

    @Transactional
    public SarvasyaQuestion create(SarvasyaQuestion sarvasyaQuestion) {
        return repository.save(sarvasyaQuestion);
    }

    public List<SarvasyaQuestion> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaQuestion> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaQuestion update(UUID id, SarvasyaQuestion updated) {
        return repository.findById(id).map(existing -> {
            existing.setType(updated.getType());
            existing.setQuestionText(updated.getQuestionText());
            existing.setExplanation(updated.getExplanation());
            existing.setMarks(updated.getMarks());
            existing.setNegativeMarks(updated.getNegativeMarks());
            existing.setNatAnswer(updated.getNatAnswer());
            existing.setNatMinRange(updated.getNatMinRange());
            existing.setNatMaxRange(updated.getNatMaxRange());
            existing.setIsRangeBased(updated.getIsRangeBased());
            existing.setTopic(updated.getTopic());
            existing.setDifficulty(updated.getDifficulty());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaQuestion not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
