package com.sarvasya.sarvasya_lms_backend.controller.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.Bus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusStopRequest;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusStopResponseDTO;
import com.sarvasya.sarvasya_lms_backend.service.bus.BusStopService;

@RestController
@RequestMapping("/sarvasya/bus-stops")
@RequiredArgsConstructor
public class BusStopController {

    private final BusStopService busStopService;

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BusStopResponseDTO> createBusStop(@RequestBody BusStopRequest request) {
        try {
            return ResponseEntity.ok(busStopService.createBusStop(request));
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<BusStopResponseDTO> getBusStopById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(busStopService.getBusStopById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasAnyAuthority('admin', 'professor', 'user')")
    public ResponseEntity<List<BusStopResponseDTO>> getStopsByBusId(@PathVariable UUID busId) {
        return ResponseEntity.ok(busStopService.getStopsByBusId(busId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<BusStopResponseDTO> updateBusStop(@PathVariable UUID id, @RequestBody BusStopRequest request) {
        try {
            return ResponseEntity.ok(busStopService.updateBusStop(id, request));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteBusStop(@PathVariable UUID id) {
        try {
            busStopService.deleteBusStop(id);
            return ResponseEntity.ok("Bus stop deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}








