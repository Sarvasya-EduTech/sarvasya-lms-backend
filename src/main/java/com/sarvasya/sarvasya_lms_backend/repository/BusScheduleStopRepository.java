package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.BusScheduleStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BusScheduleStopRepository extends JpaRepository<BusScheduleStop, UUID> {
    List<BusScheduleStop> findByScheduleIdOrderBySequenceNumber(UUID scheduleId);
    void deleteByScheduleId(UUID scheduleId);
}
