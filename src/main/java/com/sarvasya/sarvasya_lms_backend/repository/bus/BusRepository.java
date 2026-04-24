package com.sarvasya.sarvasya_lms_backend.repository.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.Bus;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, UUID> {
}








