package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaCourse;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaCourseRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaCourseService;

@Service
@RequiredArgsConstructor
public class SarvasyaCourseServiceImpl implements SarvasyaCourseService {

    private final SarvasyaCourseRepository repository;

    @Override
    @Transactional
    public SarvasyaCourse create(SarvasyaCourse sarvasyaCourse) {
        return repository.save(sarvasyaCourse);
    }

    @Override
    public List<SarvasyaCourse> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<SarvasyaCourse> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public SarvasyaCourse update(UUID id, SarvasyaCourse updated) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setDescription(updated.getDescription());
            existing.setPrice(updated.getPrice());
            existing.setCourseCode(updated.getCourseCode());
            existing.setIsActive(updated.getIsActive());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaCourse not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








