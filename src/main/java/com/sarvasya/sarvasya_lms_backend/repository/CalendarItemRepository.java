package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.CalendarItem;
import com.sarvasya.sarvasya_lms_backend.model.CalendarItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CalendarItemRepository extends JpaRepository<CalendarItem, UUID> {
    List<CalendarItem> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM CalendarItem c WHERE c.classId = :classId OR c.type IN :globalTypes")
    List<CalendarItem> findByClassIdOrGlobalTypes(@Param("classId") UUID classId, @Param("globalTypes") List<CalendarItemType> globalTypes);
}
