package com.sarvasya.sarvasya_lms_backend.service.impl.timetable;

import com.sarvasya.sarvasya_lms_backend.repository.classes.ClassesRepository;
import com.sarvasya.sarvasya_lms_backend.model.classes.Classes;
import com.sarvasya.sarvasya_lms_backend.model.course.Course;
import com.sarvasya.sarvasya_lms_backend.model.timetable.TimeTable;
import com.sarvasya.sarvasya_lms_backend.model.timetable.TimeTableEntry;
import com.sarvasya.sarvasya_lms_backend.model.attendance.AttendanceSession;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.dto.timetable.TimeTableDTO;
import com.sarvasya.sarvasya_lms_backend.model.calendar.CalendarItem;
import com.sarvasya.sarvasya_lms_backend.model.calendar.CalendarItemType;
import com.sarvasya.sarvasya_lms_backend.model.common.ReferenceType;
import com.sarvasya.sarvasya_lms_backend.repository.attendance.AttendanceSessionRepository;
import com.sarvasya.sarvasya_lms_backend.repository.calendar.CalendarItemRepository;
import com.sarvasya.sarvasya_lms_backend.repository.timetable.TimeTableRepository;
import com.sarvasya.sarvasya_lms_backend.service.timetable.TimeTableService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeTableServiceImpl implements TimeTableService {

    private final TimeTableRepository repository;
    private final CalendarItemRepository calendarItemRepository;
    private final ClassesRepository classesRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;

    private static final String COLOR_TIMETABLE = "#4CAF50"; // green for timetable entries

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

        // Sync timetable entries to the calendar
        syncTimeTableToCalendar(saved);

        return convertToDTO(saved);
    }

    @Transactional
    public void delete(UUID id) {
        TimeTable timeTable = repository.findById(id).orElse(null);
        if (timeTable != null) {
            deleteLinkedAttendanceSessions(timeTable);
        }
        // Remove all calendar items linked to this timetable
        calendarItemRepository.deleteByReferenceIdAndReferenceType(id, ReferenceType.TIMETABLE);
        repository.deleteById(id);
    }

    // ─── Calendar Sync Logic ─────────────────────────────────────────────

    /**
     * Creates/updates CalendarItem entries for each week-day occurrence of this
     * timetable within its validity period (startDate → endDate).
     *
     * Each timetable generates one CalendarItem per day that has at least one
     * scheduled period. The CalendarItem spans from the earliest startTime to the
     * latest endTime on that day and lists all courses in its description.
     *
     * Strategy:
     *  - Delete all existing CalendarItems referencing this timetable
     *  - Re-create them from the current schedule
     */
    private void syncTimeTableToCalendar(TimeTable timeTable) {
        // 1. Delete existing calendar items for this timetable
        calendarItemRepository.deleteByReferenceIdAndReferenceType(
                timeTable.getId(), ReferenceType.TIMETABLE);

        if (timeTable.getStartDate() == null || timeTable.getEndDate() == null) {
            log.info("TimeTable {} has no date range, skipping calendar sync", timeTable.getId());
            return;
        }

        if (timeTable.getEntries() == null || timeTable.getEntries().isEmpty()) {
            log.info("TimeTable {} has no entries, skipping calendar sync", timeTable.getId());
            return;
        }

        // 2. Group entries by day of week
        Map<String, List<TimeTableEntry>> entriesByDay = timeTable.getEntries().stream()
                .collect(Collectors.groupingBy(TimeTableEntry::getDayOfWeek));

        // 3. For each day that has entries, walk the date range and create CalendarItems
        LocalDate start = timeTable.getStartDate();
        LocalDate end = timeTable.getEndDate();

        for (Map.Entry<String, List<TimeTableEntry>> dayEntry : entriesByDay.entrySet()) {
            String dayName = dayEntry.getKey().toLowerCase();
            List<TimeTableEntry> periods = dayEntry.getValue();
            DayOfWeek targetDow = parseDayOfWeek(dayName);
            if (targetDow == null) continue;

            // Walk each occurrence of this day-of-week within the date range
            LocalDate current = start;
            // Advance to the first occurrence of the target day
            while (current.getDayOfWeek() != targetDow && !current.isAfter(end)) {
                current = current.plusDays(1);
            }

            String className = "Unknown Class";
            if (timeTable.getClassId() != null) {
                className = classesRepository.findById(timeTable.getClassId())
                        .map(Classes::getSubject)
                        .orElse("Unknown Class");
            }

            while (!current.isAfter(end)) {
                for (TimeTableEntry p : periods) {
                    LocalTime startTime = parseTime(p.getStartTime());
                    LocalTime endTime = parseTime(p.getEndTime());
                    String courseName = p.getCourseName() != null ? p.getCourseName() : "Free";

                    CalendarItem calItem = new CalendarItem();
                    calItem.setTitle(courseName);
                    calItem.setDescription(String.format("Course: %s\nTime: %s - %s\nDay: %s\nDate: %s\nClass: %s", 
                        courseName, p.getStartTime(), p.getEndTime(), capitalize(dayName), current.toString(), className));
                    calItem.setType(CalendarItemType.TIMETABLE);
                    calItem.setStartDateTime(LocalDateTime.of(current, startTime));
                    calItem.setEndDateTime(LocalDateTime.of(current, endTime));
                    calItem.setAllDay(false);
                    calItem.setReferenceId(timeTable.getId());
                    calItem.setReferenceType(ReferenceType.TIMETABLE);
                    calItem.setColorCode(COLOR_TIMETABLE);
                    calItem.setClassId(timeTable.getClassId());

                    calendarItemRepository.save(calItem);
                }

                current = current.plusWeeks(1);
            }
        }

        log.info("Synced timetable {} to calendar for range {} – {}",
                timeTable.getId(), start, end);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

    private DayOfWeek parseDayOfWeek(String dayName) {
        return switch (dayName.toLowerCase()) {
            case "monday" -> DayOfWeek.MONDAY;
            case "tuesday" -> DayOfWeek.TUESDAY;
            case "wednesday" -> DayOfWeek.WEDNESDAY;
            case "thursday" -> DayOfWeek.THURSDAY;
            case "friday" -> DayOfWeek.FRIDAY;
            case "saturday" -> DayOfWeek.SATURDAY;
            case "sunday" -> DayOfWeek.SUNDAY;
            default -> null;
        };
    }

    private LocalTime parseTime(String time) {
        if (time == null || time.isBlank()) return LocalTime.of(8, 0);
        try {
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            try {
                return LocalTime.parse(time);
            } catch (DateTimeParseException e2) {
                return LocalTime.of(8, 0);
            }
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void deleteLinkedAttendanceSessions(TimeTable timeTable) {
        if (timeTable.getClassId() == null ||
                timeTable.getStartDate() == null ||
                timeTable.getEndDate() == null ||
                timeTable.getEntries() == null ||
                timeTable.getEntries().isEmpty()) {
            return;
        }

        Set<UUID> attendanceIdsToDelete = new HashSet<>();
        for (TimeTableEntry entry : timeTable.getEntries()) {
            if (entry.getCourseId() == null) {
                continue;
            }
            DayOfWeek entryDow = parseDayOfWeek(entry.getDayOfWeek());
            if (entryDow == null) {
                continue;
            }

            List<AttendanceSession> candidates = attendanceSessionRepository
                    .findByClassIdAndCourseIdAndAttendanceDateBetween(
                            timeTable.getClassId(),
                            entry.getCourseId(),
                            timeTable.getStartDate(),
                            timeTable.getEndDate());

            for (AttendanceSession session : candidates) {
                if (session.getAttendanceDate() != null &&
                        session.getAttendanceDate().getDayOfWeek() == entryDow &&
                        session.getId() != null) {
                    attendanceIdsToDelete.add(session.getId());
                }
            }
        }

        if (!attendanceIdsToDelete.isEmpty()) {
            attendanceSessionRepository.deleteAllById(attendanceIdsToDelete);
            log.info("Deleted {} attendance sessions linked to timetable {}", attendanceIdsToDelete.size(), timeTable.getId());
        }
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









