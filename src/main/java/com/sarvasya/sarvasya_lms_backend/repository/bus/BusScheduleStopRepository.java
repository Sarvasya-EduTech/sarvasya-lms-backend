package com.sarvasya.sarvasya_lms_backend.repository.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.BusScheduleStop;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusScheduleStopRepository extends JpaRepository<BusScheduleStop, UUID> {
    List<BusScheduleStop> findByScheduleIdOrderBySequenceNumber(UUID scheduleId);
    void deleteByScheduleId(UUID scheduleId);
}
