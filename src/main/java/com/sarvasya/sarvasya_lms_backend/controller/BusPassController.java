package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.BusPassCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.BusPassResponseDTO;
import com.sarvasya.sarvasya_lms_backend.model.BusPass;
import com.sarvasya.sarvasya_lms_backend.service.BusPassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bus-passes")
@RequiredArgsConstructor
public class BusPassController {

    private final BusPassService busPassService;

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BusPassResponseDTO> createBusPass(@RequestBody BusPassCreateRequest request) {
        try {
            BusPassResponseDTO created = busPassService.createBusPass(request);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<BusPassResponseDTO> getBusPassById(@PathVariable UUID id) {
        try {
            BusPassResponseDTO busPass = busPassService.getBusPassById(id);
            return ResponseEntity.ok(busPass);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<List<BusPassResponseDTO>> getAllBusPasses() {
        List<BusPassResponseDTO> busPasses = busPassService.getAllBusPasses();
        return ResponseEntity.ok(busPasses);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<List<BusPassResponseDTO>> getBusPassesByUserId(@PathVariable UUID userId) {
        List<BusPassResponseDTO> busPasses = busPassService.getBusPassesByUserId(userId);
        return ResponseEntity.ok(busPasses);
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<List<BusPassResponseDTO>> getBusPassesByBusId(@PathVariable UUID busId) {
        List<BusPassResponseDTO> busPasses = busPassService.getBusPassesByBusId(busId);
        return ResponseEntity.ok(busPasses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BusPassResponseDTO> updateBusPass(@PathVariable UUID id, @RequestBody BusPass passDetails) {
        try {
            BusPassResponseDTO updated = busPassService.updateBusPass(id, passDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteBusPass(@PathVariable UUID id) {
        try {
            busPassService.deleteBusPass(id);
            return ResponseEntity.ok("Bus Pass deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
