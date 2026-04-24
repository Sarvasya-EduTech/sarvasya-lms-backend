package com.sarvasya.sarvasya_lms_backend.repository.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.BusSchedule;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusScheduleRepository extends JpaRepository<BusSchedule, UUID> {
    List<BusSchedule> findByBusId(UUID busId);
}








