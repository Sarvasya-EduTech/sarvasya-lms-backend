package com.sarvasya.sarvasya_lms_backend.controller.attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.attendance.AttendanceBookedSlotDTO;
import com.sarvasya.sarvasya_lms_backend.dto.attendance.AttendanceBulkDeleteRequest;
import com.sarvasya.sarvasya_lms_backend.dto.attendance.AttendanceBulkUpdateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.attendance.AttendanceUpsertRequest;
import com.sarvasya.sarvasya_lms_backend.model.attendance.AttendanceSession;
import com.sarvasya.sarvasya_lms_backend.service.attendance.AttendanceService;

@RestController
@RequestMapping("/{tenantName}/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor', 'user', 'student')")
    public ResponseEntity<List<AttendanceSession>> getAll(
            @PathVariable("tenantName") String tenantName,
            @RequestParam(value = "classId", required = false) UUID classId,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "date", required = false) LocalDate date
    ) {
        return ResponseEntity.ok(attendanceService.list(classId, courseId, date));
    }

    @GetMapping("/booked-slots")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor', 'user', 'student')")
    public ResponseEntity<List<AttendanceBookedSlotDTO>> getBookedSlots(
            @PathVariable("tenantName") String tenantName,
            @RequestParam("classId") UUID classId,
            @RequestParam("courseId") UUID courseId,
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate
    ) {
        return ResponseEntity.ok(attendanceService.listBookedSlots(classId, courseId, fromDate, toDate));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<AttendanceSession> create(
            @PathVariable("tenantName") String tenantName,
            @RequestBody AttendanceUpsertRequest request) {
        return ResponseEntity.ok(attendanceService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<AttendanceSession> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody AttendanceUpsertRequest request) {
        return ResponseEntity.ok(attendanceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<?> delete(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id) {
        attendanceService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-delete")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<?> bulkDelete(
            @PathVariable("tenantName") String tenantName,
            @RequestBody AttendanceBulkDeleteRequest request) {
        attendanceService.bulkDelete(request.getIds());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-update")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<List<AttendanceSession>> bulkUpdate(
            @PathVariable("tenantName") String tenantName,
            @RequestBody AttendanceBulkUpdateRequest request) {
        return ResponseEntity.ok(attendanceService.bulkUpdateNotes(request.getSessionIds(), request.getNotes()));
    }
}








