package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.FeeComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeeComponentRepository extends JpaRepository<FeeComponent, UUID> {
    List<FeeComponent> findByFeeStructureId(UUID feeStructureId);
    void deleteByFeeStructureId(UUID feeStructureId);
}
