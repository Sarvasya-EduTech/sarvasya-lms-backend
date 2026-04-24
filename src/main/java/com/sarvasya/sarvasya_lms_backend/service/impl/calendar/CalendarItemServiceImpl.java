package com.sarvasya.sarvasya_lms_backend.service.impl.calendar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.model.calendar.CalendarItem;
import com.sarvasya.sarvasya_lms_backend.model.calendar.CalendarItemType;
import com.sarvasya.sarvasya_lms_backend.repository.calendar.CalendarItemRepository;
import com.sarvasya.sarvasya_lms_backend.service.calendar.CalendarItemService;

@Service
@RequiredArgsConstructor
public class CalendarItemServiceImpl implements CalendarItemService {

    private final CalendarItemRepository repository;

    public List<CalendarItem> findAll() {
        return repository.findAll();
    }

    public List<CalendarItem> findBetween(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return repository.findByStartDateTimeBetween(start, end);
        }
        return findAll();
    }

    public List<CalendarItem> findForStudent(UUID classId) {
        List<CalendarItemType> globalTypes = List.of(CalendarItemType.EVENT, CalendarItemType.HOLIDAY, CalendarItemType.TIMETABLE);
        if (classId == null) {
            // If student has no class assigned, only show global items
            return repository.findAll().stream()
                    .filter(item -> globalTypes.contains(item.getType()))
                    .toList();
        }
        return repository.findByClassIdOrGlobalTypes(classId, globalTypes);
    }

    public CalendarItem save(CalendarItem item) {
        return repository.save(item);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}









