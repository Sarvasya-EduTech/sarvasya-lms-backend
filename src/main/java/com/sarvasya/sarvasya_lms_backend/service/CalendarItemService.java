package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.CalendarItem;
import com.sarvasya.sarvasya_lms_backend.repository.CalendarItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarItemService {

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

    public CalendarItem save(CalendarItem item) {
        return repository.save(item);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
