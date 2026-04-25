package com.sarvasya.sarvasya_lms_backend.service.impl.degree;

import com.sarvasya.sarvasya_lms_backend.model.degree.Degree;
import com.sarvasya.sarvasya_lms_backend.model.degree.Degree;
import com.sarvasya.sarvasya_lms_backend.repository.degree.DegreeRepository;
import com.sarvasya.sarvasya_lms_backend.service.degree.DegreeService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DegreeServiceImpl implements DegreeService {

    private final DegreeRepository repository;

    public List<Degree> findAll() {
        return repository.findAll();
    }

    public Degree save(Degree degree) {
        return repository.save(degree);
    }

    public java.util.Optional<Degree> findById(UUID id) {
        return repository.findById(id);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








