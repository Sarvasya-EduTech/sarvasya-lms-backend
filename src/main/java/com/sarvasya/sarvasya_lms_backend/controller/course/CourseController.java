package com.sarvasya.sarvasya_lms_backend.controller.course;

import com.sarvasya.sarvasya_lms_backend.model.course.Course;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.service.course.CourseService;

@RestController
@RequestMapping("/{tenantName}/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    @GetMapping
    public ResponseEntity<List<Course>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Course>> getByDepartment(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("departmentId") UUID departmentId) {
        return ResponseEntity.ok(service.findByDepartmentId(departmentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Course> create(
            @PathVariable("tenantName") String tenantName,
            @RequestBody Course course) {
        return ResponseEntity.ok(service.save(course));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Course> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody Course course) {
        course.setId(id);
        return ResponseEntity.ok(service.save(course));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<?> delete(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








