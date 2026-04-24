package com.sarvasya.sarvasya_lms_backend.service.department;

import com.sarvasya.sarvasya_lms_backend.model.department.Department;
import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    List<Department> findAll();

    Department save(Department department);

    void delete(UUID id);

}








