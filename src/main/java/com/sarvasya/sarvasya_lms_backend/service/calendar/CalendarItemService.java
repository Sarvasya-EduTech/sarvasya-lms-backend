package com.sarvasya.sarvasya_lms_backend.service.calendar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.calendar.CalendarItem;

public interface CalendarItemService {
    List<CalendarItem> findAll();

    List<CalendarItem> findBetween(LocalDateTime start, LocalDateTime end);

    List<CalendarItem> findForStudent(UUID classId);

    CalendarItem save(CalendarItem item);

    void delete(UUID id);

}









