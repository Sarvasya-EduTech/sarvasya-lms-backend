package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.BusSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BusScheduleRepository extends JpaRepository<BusSchedule, UUID> {
    List<BusSchedule> findByBusId(UUID busId);
}
