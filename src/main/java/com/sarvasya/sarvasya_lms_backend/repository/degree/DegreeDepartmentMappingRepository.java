package com.sarvasya.sarvasya_lms_backend.repository.degree;

import com.sarvasya.sarvasya_lms_backend.model.degree.DegreeDepartmentMapping;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DegreeDepartmentMappingRepository extends JpaRepository<DegreeDepartmentMapping, UUID> {
    List<DegreeDepartmentMapping> findByDegreeId(UUID degreeId);
    boolean existsByDegreeIdAndDepartmentId(UUID degreeId, UUID departmentId);
    void deleteByDegreeIdAndDepartmentId(UUID degreeId, UUID departmentId);
}








