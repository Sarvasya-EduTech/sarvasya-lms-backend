package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.BusScheduleResponseDTO;
import com.sarvasya.sarvasya_lms_backend.model.BusSchedule;
import com.sarvasya.sarvasya_lms_backend.service.BusScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bus-schedules")
@RequiredArgsConstructor
public class BusScheduleController {

    private final BusScheduleService busScheduleService;

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BusScheduleResponseDTO> createBusSchedule(@RequestBody BusSchedule busSchedule) {
        try {
            BusScheduleResponseDTO created = busScheduleService.createBusSchedule(busSchedule);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<BusScheduleResponseDTO> getBusScheduleById(@PathVariable UUID id) {
        try {
            BusScheduleResponseDTO schedule = busScheduleService.getBusScheduleById(id);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<List<BusScheduleResponseDTO>> getAllBusSchedules() {
        List<BusScheduleResponseDTO> schedules = busScheduleService.getAllBusSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<List<BusScheduleResponseDTO>> getBusSchedulesByBusId(@PathVariable UUID busId) {
        List<BusScheduleResponseDTO> schedules = busScheduleService.getBusSchedulesByBusId(busId);
        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BusScheduleResponseDTO> updateBusSchedule(@PathVariable UUID id,
            @RequestBody BusSchedule scheduleDetails) {
        try {
            BusScheduleResponseDTO updated = busScheduleService.updateBusSchedule(id, scheduleDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteBusSchedule(@PathVariable UUID id) {
        try {
            busScheduleService.deleteBusSchedule(id);
            return ResponseEntity.ok("Bus Schedule deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
