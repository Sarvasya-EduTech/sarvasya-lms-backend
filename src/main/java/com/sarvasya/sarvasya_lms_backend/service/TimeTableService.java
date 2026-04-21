package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.TimeTableDTO;
import com.sarvasya.sarvasya_lms_backend.model.TimeTable;
import com.sarvasya.sarvasya_lms_backend.model.TimeTableEntry;
import com.sarvasya.sarvasya_lms_backend.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final TimeTableRepository repository;

    public List<TimeTableDTO> findAll() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TimeTableDTO save(TimeTableDTO dto) {
        TimeTable timeTable;
        if (dto.getId() != null) {
            timeTable = repository.findById(dto.getId()).orElse(new TimeTable());
        } else {
            timeTable = new TimeTable();
        }

        timeTable.setClassId(dto.getClassId());
        timeTable.setStartDate(dto.getStartDate() != null ? LocalDate.parse(dto.getStartDate()) : null);
        timeTable.setEndDate(dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate()) : null);

        // Clear existing entries
        if (timeTable.getEntries() != null) {
            timeTable.getEntries().clear();
        } else {
            timeTable.setEntries(new ArrayList<>());
        }

        // Map DTO schedule to entries
        if (dto.getSchedule() != null) {
            for (Map.Entry<String, List<TimeTableDTO.PeriodDTO>> entry : dto.getSchedule().entrySet()) {
                String day = entry.getKey();
                for (TimeTableDTO.PeriodDTO period : entry.getValue()) {
                    TimeTableEntry modelEntry = new TimeTableEntry();
                    modelEntry.setTimeTable(timeTable);
                    modelEntry.setDayOfWeek(day);
                    modelEntry.setCourseName(period.getCourseName());
                    modelEntry.setCourseId(period.getCourseId());
                    modelEntry.setStartTime(period.getStartTime());
                    modelEntry.setEndTime(period.getEndTime());
                    timeTable.getEntries().add(modelEntry);
                }
            }
        }

        TimeTable saved = repository.save(timeTable);
        return convertToDTO(saved);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private TimeTableDTO convertToDTO(TimeTable model) {
        TimeTableDTO dto = new TimeTableDTO();
        dto.setId(model.getId());
        dto.setClassId(model.getClassId());
        dto.setStartDate(model.getStartDate() != null ? model.getStartDate().toString() : null);
        dto.setEndDate(model.getEndDate() != null ? model.getEndDate().toString() : null);

        Map<String, List<TimeTableDTO.PeriodDTO>> schedule = new HashMap<>();
        if (model.getEntries() != null) {
            for (TimeTableEntry entry : model.getEntries()) {
                schedule.computeIfAbsent(entry.getDayOfWeek(), k -> new ArrayList<>())
                        .add(new TimeTableDTO.PeriodDTO(
                                entry.getId(),
                                entry.getCourseId(),
                                entry.getCourseName(),
                                entry.getStartTime(),
                                entry.getEndTime()
                        ));
            }
        }
        dto.setSchedule(schedule);
        return dto;
    }
}
