package com.sarvasya.sarvasya_lms_backend.repository.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.BusStop;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, UUID> {
    List<BusStop> findByBusId(UUID busId);
    Optional<BusStop> findByBusIdAndStopName(UUID busId, String stopName);
}








