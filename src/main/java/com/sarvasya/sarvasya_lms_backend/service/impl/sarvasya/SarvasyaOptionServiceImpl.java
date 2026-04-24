package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaOption;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaOptionRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaOptionService;

@Service
@RequiredArgsConstructor
public class SarvasyaOptionServiceImpl implements SarvasyaOptionService {

    private final SarvasyaOptionRepository repository;

    @Transactional
    public SarvasyaOption create(SarvasyaOption sarvasyaOption) {
        return repository.save(sarvasyaOption);
    }

    public List<SarvasyaOption> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaOption> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaOption update(UUID id, SarvasyaOption updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaOption not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








