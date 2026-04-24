package com.sarvasya.sarvasya_lms_backend.repository.fee;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeeComponent;

public interface FeeComponentRepository extends JpaRepository<FeeComponent, UUID> {
    List<FeeComponent> findByFeeStructureId(UUID feeStructureId);
    void deleteByFeeStructureId(UUID feeStructureId);
}








