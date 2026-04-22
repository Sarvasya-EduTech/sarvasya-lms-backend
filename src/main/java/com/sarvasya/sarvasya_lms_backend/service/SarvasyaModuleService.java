package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaModule;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaModuleService {

    private final SarvasyaModuleRepository repository;

    @Transactional
    public SarvasyaModule create(SarvasyaModule sarvasyaModule) {
        return repository.save(sarvasyaModule);
    }

    public List<SarvasyaModule> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaModule> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaModule update(UUID id, SarvasyaModule updated) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setOrderIndex(updated.getOrderIndex());
            existing.setCourseId(updated.getCourseId());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaModule not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
