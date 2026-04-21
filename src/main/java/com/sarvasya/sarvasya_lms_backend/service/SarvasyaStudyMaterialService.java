package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaStudyMaterial;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaStudyMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaStudyMaterialService {

    private final SarvasyaStudyMaterialRepository repository;

    @Transactional
    public SarvasyaStudyMaterial create(SarvasyaStudyMaterial sarvasyaStudyMaterial) {
        return repository.save(sarvasyaStudyMaterial);
    }

    public List<SarvasyaStudyMaterial> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaStudyMaterial> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaStudyMaterial update(UUID id, SarvasyaStudyMaterial updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaStudyMaterial not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
