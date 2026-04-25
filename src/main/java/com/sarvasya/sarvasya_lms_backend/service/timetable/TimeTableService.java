package com.sarvasya.sarvasya_lms_backend.service.timetable;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import com.sarvasya.sarvasya_lms_backend.dto.timetable.TimeTableDTO;

public interface TimeTableService {
    List<TimeTableDTO> findAll();

    TimeTableDTO save(TimeTableDTO dto);

    void delete(UUID id);

}








