package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.BusPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusPassRepository extends JpaRepository<BusPass, UUID> {
    List<BusPass> findByUserId(UUID userId);

    List<BusPass> findByBusId(UUID busId);
}
