package com.sarvasya.sarvasya_lms_backend.service.course;

import com.sarvasya.sarvasya_lms_backend.model.course.Course;
import java.util.List;
import java.util.UUID;

public interface CourseService {
    List<Course> findAll();

    List<Course> findByDepartmentId(UUID departmentId);

    Course save(Course course);

    void delete(UUID id);

}








