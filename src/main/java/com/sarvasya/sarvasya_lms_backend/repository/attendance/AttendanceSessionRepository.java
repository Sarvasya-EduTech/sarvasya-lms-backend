package com.sarvasya.sarvasya_lms_backend.repository.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.attendance.AttendanceSession;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, UUID> {
    List<AttendanceSession> findByClassId(UUID classId);
    List<AttendanceSession> findByCourseId(UUID courseId);
    List<AttendanceSession> findByAttendanceDate(LocalDate attendanceDate);
    List<AttendanceSession> findByProfessorId(UUID professorId);
    List<AttendanceSession> findByClassIdAndCourseId(UUID classId, UUID courseId);
    Optional<AttendanceSession> findByClassIdAndCourseIdAndAttendanceDate(UUID classId, UUID courseId, LocalDate attendanceDate);
    List<AttendanceSession> findByClassIdAndCourseIdAndAttendanceDateBetween(
            UUID classId,
            UUID courseId,
            LocalDate startDate,
            LocalDate endDate
    );
}








