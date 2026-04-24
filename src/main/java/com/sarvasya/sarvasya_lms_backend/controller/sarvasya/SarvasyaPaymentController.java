package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaPayment;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaPaymentService;

@RestController
@RequestMapping("/sarvasya/{tenantName}/payments")
@RequiredArgsConstructor
public class SarvasyaPaymentController {

    private final SarvasyaPaymentService service;

    @PostMapping
    public ResponseEntity<SarvasyaPayment> create(@RequestBody SarvasyaPayment payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaPayment>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaPayment> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaPayment> update(@PathVariable UUID id, @RequestBody SarvasyaPayment payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








