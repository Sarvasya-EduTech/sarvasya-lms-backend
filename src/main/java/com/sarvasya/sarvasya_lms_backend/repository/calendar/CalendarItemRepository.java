package com.sarvasya.sarvasya_lms_backend.repository.calendar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.calendar.CalendarItem;
import com.sarvasya.sarvasya_lms_backend.model.calendar.CalendarItemType;
import com.sarvasya.sarvasya_lms_backend.model.common.ReferenceType;

@Repository
public interface CalendarItemRepository extends JpaRepository<CalendarItem, UUID> {
    List<CalendarItem> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM CalendarItem c WHERE c.classId = :classId OR c.type IN (:globalTypes)")
    List<CalendarItem> findByClassIdOrGlobalTypes(@Param("classId") UUID classId, @Param("globalTypes") List<CalendarItemType> globalTypes);

    List<CalendarItem> findByReferenceIdAndReferenceType(UUID referenceId, ReferenceType referenceType);

    void deleteByReferenceIdAndReferenceType(UUID referenceId, ReferenceType referenceType);
}









