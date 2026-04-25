package com.sarvasya.sarvasya_lms_backend.repository.timetable;

import com.sarvasya.sarvasya_lms_backend.model.timetable.TimeTable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, UUID> {
    List<TimeTable> findByClassId(UUID classId);
}








