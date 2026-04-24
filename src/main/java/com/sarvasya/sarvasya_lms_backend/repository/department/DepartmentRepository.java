package com.sarvasya.sarvasya_lms_backend.repository.department;

import com.sarvasya.sarvasya_lms_backend.model.department.Department;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
}








