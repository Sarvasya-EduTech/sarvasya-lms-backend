package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.Classes;
import com.sarvasya.sarvasya_lms_backend.repository.ClassesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClassesService {

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
}
