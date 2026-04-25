package com.sarvasya.sarvasya_lms_backend.repository.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.BusPass;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusPassRepository extends JpaRepository<BusPass, UUID> {
    List<BusPass> findByUserId(UUID userId);

    List<BusPass> findByBusId(UUID busId);
}








