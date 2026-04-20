package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.Course;
import com.sarvasya.sarvasya_lms_backend.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repository;

    public List<Course> findAll() {
        return repository.findAll();
    }

    public List<Course> findByDepartmentId(UUID departmentId) {
        return repository.findByDepartmentId(departmentId);
    }

    public Course save(Course course) {
        return repository.save(course);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
