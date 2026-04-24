package com.sarvasya.sarvasya_lms_backend.service.impl.department;

import com.sarvasya.sarvasya_lms_backend.model.department.Department;
import com.sarvasya.sarvasya_lms_backend.model.department.Department;
import com.sarvasya.sarvasya_lms_backend.repository.department.DepartmentRepository;
import com.sarvasya.sarvasya_lms_backend.service.department.DepartmentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    public List<Department> findAll() {
        return repository.findAll();
    }

    public Department save(Department department) {
        return repository.save(department);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








