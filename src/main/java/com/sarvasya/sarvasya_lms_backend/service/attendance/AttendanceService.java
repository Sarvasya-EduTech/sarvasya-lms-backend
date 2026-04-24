package com.sarvasya.sarvasya_lms_backend.service.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.dto.attendance.AttendanceBookedSlotDTO;
import com.sarvasya.sarvasya_lms_backend.dto.attendance.AttendanceUpsertRequest;
import com.sarvasya.sarvasya_lms_backend.model.attendance.AttendanceSession;

public interface AttendanceService {
    List<AttendanceSession> list(UUID classId, UUID courseId, LocalDate attendanceDate);

    List<AttendanceBookedSlotDTO> listBookedSlots(UUID classId, UUID courseId, LocalDate fromDate, LocalDate toDate);

    AttendanceSession create(AttendanceUpsertRequest request);

    AttendanceSession update(UUID id, AttendanceUpsertRequest request);

    void delete(UUID id);

    void bulkDelete(List<UUID> ids);

    List<AttendanceSession> bulkUpdateNotes(List<UUID> ids, String notes);
}








