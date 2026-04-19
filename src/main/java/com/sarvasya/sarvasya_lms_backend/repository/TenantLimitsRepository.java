package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.TenantLimits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantLimitsRepository extends JpaRepository<TenantLimits, Long> {
    TenantLimits findFirstByOrderByIdAsc();
}
