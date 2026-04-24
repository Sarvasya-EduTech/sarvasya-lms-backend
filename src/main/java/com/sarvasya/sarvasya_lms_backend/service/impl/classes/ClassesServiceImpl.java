package com.sarvasya.sarvasya_lms_backend.service.impl.classes;

import com.sarvasya.sarvasya_lms_backend.model.classes.Classes;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.repository.classes.ClassesRepository;
import com.sarvasya.sarvasya_lms_backend.service.classes.ClassesService;

@Service
@RequiredArgsConstructor
public class ClassesServiceImpl implements ClassesService {

    private final ClassesRepository repository;

    public List<Classes> findAll() {
        return repository.findAll();
    }

    public Classes save(Classes item) {
        return repository.save(item);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Classes findById(UUID id) {
        return repository.findById(id).orElse(null);
    }
}








