package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.CalendarItem;
import com.sarvasya.sarvasya_lms_backend.service.CalendarItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{tenantName}/calendar")
@RequiredArgsConstructor
public class CalendarItemController {

    private final CalendarItemService service;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor', 'user')")
    public ResponseEntity<List<CalendarItem>> getAll(
            @PathVariable("tenantName") String tenantName,
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(service.findBetween(start, end));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor', 'user')")
    public ResponseEntity<List<CalendarItem>> getMyCalendarItems(
            @PathVariable("tenantName") String tenantName,
            @RequestParam(value = "classId", required = false) UUID classId) {
        return ResponseEntity.ok(service.findForStudent(classId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<CalendarItem> create(
            @PathVariable("tenantName") String tenantName, 
            @RequestBody CalendarItem item) {
        return ResponseEntity.ok(service.save(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<CalendarItem> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody CalendarItem item) {
        item.setId(id);
        return ResponseEntity.ok(service.save(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<?> delete(
            @PathVariable("tenantName") String tenantName, 
            @PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
