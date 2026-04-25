package com.sarvasya.sarvasya_lms_backend.controller.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.Bus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.service.bus.BusService;

@RestController
@RequestMapping("/sarvasya/buses")
@RequiredArgsConstructor
public class BusController {

    private static final Logger logger = LoggerFactory.getLogger(BusController.class);
    private final BusService busService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Bus> createBus(@RequestBody Bus bus) {
        logger.info("Creating bus: {}", bus);
        try {
            Bus createdBus = busService.createBus(bus);
            logger.info("Bus created successfully: {}", createdBus.getId());
            return ResponseEntity.ok(createdBus);
        } catch (Exception e) {
            logger.error("Error creating bus", e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Bus> getBusById(@PathVariable UUID id) {
        try {
            Bus bus = busService.getBusById(id);
            return ResponseEntity.ok(bus);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Bus>> getAllBuses() {
        List<Bus> buses = busService.getAllBuses();
        return ResponseEntity.ok(buses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Bus> updateBus(@PathVariable UUID id, @RequestBody Bus busDetails) {
        try {
            Bus updatedBus = busService.updateBus(id, busDetails);
            return ResponseEntity.ok(updatedBus);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<?> deleteBus(@PathVariable UUID id) {
        try {
            busService.deleteBus(id);
            return ResponseEntity.ok("Bus deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}








