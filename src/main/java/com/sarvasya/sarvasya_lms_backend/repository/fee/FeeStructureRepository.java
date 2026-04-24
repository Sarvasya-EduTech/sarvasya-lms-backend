package com.sarvasya.sarvasya_lms_backend.repository.fee;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeeStructure;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, UUID> {
    List<FeeStructure> findByDegreeIdAndDepartmentIdAndSemesterAndIsActive(UUID degreeId, UUID departmentId, Integer semester, boolean isActive);
    List<FeeStructure> findByIsActive(boolean isActive);
    List<FeeStructure> findByClassId(UUID classId);
    List<FeeStructure> findByClassIdAndIsActive(UUID classId, boolean isActive);
}








