package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.CalendarItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CalendarItemRepository extends JpaRepository<CalendarItem, UUID> {
    List<CalendarItem> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
