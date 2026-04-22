package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.DegreeDepartmentMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DegreeDepartmentMappingRepository extends JpaRepository<DegreeDepartmentMapping, UUID> {
    List<DegreeDepartmentMapping> findByDegreeId(UUID degreeId);
    boolean existsByDegreeIdAndDepartmentId(UUID degreeId, UUID departmentId);
    void deleteByDegreeIdAndDepartmentId(UUID degreeId, UUID departmentId);
}
