package com.sarvasya.sarvasya_lms_backend.controller.timetable;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.timetable.TimeTableDTO;
import com.sarvasya.sarvasya_lms_backend.service.timetable.TimeTableService;

@RestController
@RequestMapping("/{tenantName}/timetables")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService service;

    @GetMapping
    public ResponseEntity<List<TimeTableDTO>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<TimeTableDTO> create(
            @PathVariable("tenantName") String tenantName,
            @RequestBody TimeTableDTO item) {
        return ResponseEntity.ok(service.save(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<TimeTableDTO> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody TimeTableDTO item) {
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








