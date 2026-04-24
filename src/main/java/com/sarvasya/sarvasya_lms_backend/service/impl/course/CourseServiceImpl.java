package com.sarvasya.sarvasya_lms_backend.service.impl.course;

import com.sarvasya.sarvasya_lms_backend.model.course.Course;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.repository.course.CourseRepository;
import com.sarvasya.sarvasya_lms_backend.service.course.CourseService;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

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








