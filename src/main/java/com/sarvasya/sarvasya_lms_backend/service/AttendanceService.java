package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.AttendanceBookedSlotDTO;
import com.sarvasya.sarvasya_lms_backend.dto.AttendanceUpsertRequest;
import com.sarvasya.sarvasya_lms_backend.model.AttendanceRecord;
import com.sarvasya.sarvasya_lms_backend.model.AttendanceSession;
import com.sarvasya.sarvasya_lms_backend.model.AttendanceStatus;
import com.sarvasya.sarvasya_lms_backend.model.TimeTable;
import com.sarvasya.sarvasya_lms_backend.model.TimeTableEntry;
import com.sarvasya.sarvasya_lms_backend.repository.AttendanceSessionRepository;
import com.sarvasya.sarvasya_lms_backend.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final TimeTableRepository timeTableRepository;
    private final StudentIdResolver studentIdResolver;
    private final ProfessorAssignmentService professorAssignmentService;

    public List<AttendanceSession> list(UUID classId, UUID courseId, LocalDate attendanceDate) {
        List<AttendanceSession> sessions = attendanceSessionRepository.findAll();
        if (classId != null) {
            sessions = sessions.stream().filter(s -> classId.equals(s.getClassId())).collect(Collectors.toList());
        }
        if (courseId != null) {
            sessions = sessions.stream().filter(s -> courseId.equals(s.getCourseId())).collect(Collectors.toList());
        }
        if (attendanceDate != null) {
            sessions = sessions.stream().filter(s -> attendanceDate.equals(s.getAttendanceDate())).collect(Collectors.toList());
        }

        if (isStudent()) {
            UUID studentId = currentUserId();
            return sessions.stream()
                    .map(s -> filterForStudent(s, studentId))
                    .filter(s -> !s.getRecords().isEmpty())
                    .collect(Collectors.toList());
        }
        if (isProfessor()) {
            UUID professorId = currentUserId();
            return sessions.stream()
                    .filter(s -> professorId.equals(s.getProfessorId()) ||
                            professorAssignmentService.isProfessorAssigned(professorId, s.getClassId(), s.getCourseId()))
                    .collect(Collectors.toList());
        }
        return sessions;
    }

    public List<AttendanceBookedSlotDTO> listBookedSlots(UUID classId, UUID courseId, LocalDate fromDate, LocalDate toDate) {
        if (classId == null || courseId == null) {
            throw new IllegalArgumentException("classId and courseId are required");
        }

        LocalDate effectiveFrom = fromDate != null ? fromDate : LocalDate.now();
        LocalDate effectiveTo = toDate != null ? toDate : effectiveFrom.plusDays(30);
        if (effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("toDate must be on or after fromDate");
        }

        List<AttendanceBookedSlotDTO> slots = new ArrayList<>();
        List<TimeTable> timeTables = timeTableRepository.findByClassId(classId);
        for (TimeTable timeTable : timeTables) {
            if (timeTable.getStartDate() == null || timeTable.getEndDate() == null || timeTable.getEntries() == null) {
                continue;
            }

            LocalDate windowStart = maxDate(timeTable.getStartDate(), effectiveFrom);
            LocalDate windowEnd = minDate(timeTable.getEndDate(), effectiveTo);
            if (windowEnd.isBefore(windowStart)) {
                continue;
            }

            for (TimeTableEntry entry : timeTable.getEntries()) {
                if (!courseId.equals(entry.getCourseId())) {
                    continue;
                }
                DayOfWeek dayOfWeek = parseDay(entry.getDayOfWeek());
                if (dayOfWeek == null) {
                    continue;
                }

                LocalDate firstMatch = firstMatchingDate(windowStart, dayOfWeek);
                for (LocalDate slotDate = firstMatch; !slotDate.isAfter(windowEnd); slotDate = slotDate.plusWeeks(1)) {
                    slots.add(new AttendanceBookedSlotDTO(
                            timeTable.getId(),
                            entry.getId(),
                            classId,
                            courseId,
                            entry.getCourseName(),
                            entry.getDayOfWeek(),
                            slotDate,
                            entry.getStartTime(),
                            entry.getEndTime()
                    ));
                }
            }
        }

        return slots.stream()
                .sorted(Comparator
                        .comparing(AttendanceBookedSlotDTO::getSlotDate)
                        .thenComparing(AttendanceBookedSlotDTO::getStartTime, Comparator.nullsLast(String::compareTo))
                        .thenComparing(AttendanceBookedSlotDTO::getEndTime, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceSession create(AttendanceUpsertRequest request) {
        UUID currentUserId = currentUserId();
        if (isProfessor() && !isAdmin()) {
            if (!professorAssignmentService.isProfessorAssigned(currentUserId, request.getClassId(), request.getCourseId())) {
                throw new IllegalArgumentException("Professor is not assigned to this class-course");
            }
        }

        AttendanceSession existing = attendanceSessionRepository
                .findByClassIdAndCourseIdAndAttendanceDate(request.getClassId(), request.getCourseId(), request.getAttendanceDate())
                .orElse(null);

        AttendanceSession session = existing != null ? existing : new AttendanceSession();
        session.setClassId(request.getClassId());
        session.setCourseId(request.getCourseId());
        session.setAttendanceDate(request.getAttendanceDate());
        session.setNotes(request.getNotes());
        if (existing == null) {
            session.setProfessorId(isAdmin() && request.getClassId() != null ? currentUserId : currentUserId);
        }

        rebuildRecords(session, request);
        return attendanceSessionRepository.save(session);
    }

    @Transactional
    public AttendanceSession update(UUID id, AttendanceUpsertRequest request) {
        AttendanceSession session = attendanceSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance session not found"));
        UUID currentUserId = currentUserId();
        if (isProfessor() && !isAdmin() && !currentUserId.equals(session.getProfessorId())) {
            throw new IllegalArgumentException("Professors can only edit their own attendance entries");
        }
        if (isProfessor() && !isAdmin() &&
                !professorAssignmentService.isProfessorAssigned(currentUserId, request.getClassId(), request.getCourseId())) {
            throw new IllegalArgumentException("Professor is not assigned to this class-course");
        }
        session.setClassId(request.getClassId());
        session.setCourseId(request.getCourseId());
        session.setAttendanceDate(request.getAttendanceDate());
        session.setNotes(request.getNotes());
        rebuildRecords(session, request);
        return attendanceSessionRepository.save(session);
    }

    @Transactional
    public void delete(UUID id) {
        AttendanceSession session = attendanceSessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance session not found"));
        UUID currentUserId = currentUserId();
        if (isProfessor() && !isAdmin() && !currentUserId.equals(session.getProfessorId())) {
            throw new IllegalArgumentException("Professors can only delete their own attendance entries");
        }
        attendanceSessionRepository.deleteById(id);
    }

    @Transactional
    public void bulkDelete(List<UUID> ids) {
        if (!isAdmin()) {
            throw new IllegalArgumentException("Only admin and sarvasya-admin can perform bulk delete");
        }
        if (ids == null || ids.isEmpty()) return;
        attendanceSessionRepository.deleteAllById(ids);
    }

    @Transactional
    public List<AttendanceSession> bulkUpdateNotes(List<UUID> ids, String notes) {
        if (!isAdmin()) {
            throw new IllegalArgumentException("Only admin and sarvasya-admin can perform bulk update");
        }
        if (ids == null || ids.isEmpty()) return List.of();
        List<AttendanceSession> sessions = attendanceSessionRepository.findAllById(ids);
        for (AttendanceSession session : sessions) {
            session.setNotes(notes);
        }
        return attendanceSessionRepository.saveAll(sessions);
    }

    private void rebuildRecords(AttendanceSession session, AttendanceUpsertRequest request) {
        if (session.getRecords() == null) {
            session.setRecords(new ArrayList<>());
        }
        session.getRecords().clear();
        if (request.getRecords() == null) return;

        for (AttendanceUpsertRequest.RecordRequest recordRequest : request.getRecords()) {
            AttendanceRecord record = new AttendanceRecord();
            record.setAttendanceSession(session);
            record.setStudentId(recordRequest.getStudentId());
            record.setStatus(recordRequest.getStatus() == null ? AttendanceStatus.ABSENT : recordRequest.getStatus());
            record.setRemarks(recordRequest.getRemarks());
            session.getRecords().add(record);
        }
    }

    private AttendanceSession filterForStudent(AttendanceSession session, UUID studentId) {
        AttendanceSession clone = new AttendanceSession();
        clone.setId(session.getId());
        clone.setClassId(session.getClassId());
        clone.setCourseId(session.getCourseId());
        clone.setProfessorId(session.getProfessorId());
        clone.setAttendanceDate(session.getAttendanceDate());
        clone.setNotes(session.getNotes());
        List<AttendanceRecord> ownRecords = session.getRecords() == null
                ? new ArrayList<>()
                : session.getRecords().stream()
                .filter(r -> studentId.equals(r.getStudentId()))
                .collect(Collectors.toList());
        clone.setRecords(ownRecords);
        return clone;
    }

    private UUID currentUserId() {
        return studentIdResolver.resolveCurrentUserId()
                .orElseThrow(() -> new IllegalArgumentException("Unable to resolve current user"));
    }

    private boolean isStudent() {
        return hasAuthority("user") || hasAuthority("student");
    }

    private boolean isProfessor() {
        return hasAuthority("professor");
    }

    private boolean isAdmin() {
        return hasAuthority("admin") || hasAuthority("sarvasya-admin");
    }

    private boolean hasAuthority(String authority) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> authority.equalsIgnoreCase(a.getAuthority()));
    }

    private DayOfWeek parseDay(String dayValue) {
        if (dayValue == null || dayValue.isBlank()) return null;
        try {
            return DayOfWeek.valueOf(dayValue.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private LocalDate firstMatchingDate(LocalDate from, DayOfWeek targetDay) {
        LocalDate date = from;
        while (date.getDayOfWeek() != targetDay) {
            date = date.plusDays(1);
        }
        return date;
    }

    private LocalDate maxDate(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private LocalDate minDate(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }
}
