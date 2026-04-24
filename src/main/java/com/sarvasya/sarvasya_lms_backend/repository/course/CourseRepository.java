package com.sarvasya.sarvasya_lms_backend.repository.course;

import com.sarvasya.sarvasya_lms_backend.model.course.Course;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByDepartmentId(UUID departmentId);
}








