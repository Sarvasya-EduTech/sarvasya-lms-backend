package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, UUID> {
    List<BusStop> findByBusId(UUID busId);
    Optional<BusStop> findByBusIdAndStopName(UUID busId, String stopName);
}
