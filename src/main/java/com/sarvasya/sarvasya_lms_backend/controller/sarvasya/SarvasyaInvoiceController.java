package com.sarvasya.sarvasya_lms_backend.controller.sarvasya;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaInvoice;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaInvoiceService;

@RestController
@RequestMapping("/sarvasya/{tenantName}/invoices")
@RequiredArgsConstructor
public class SarvasyaInvoiceController {

    private final SarvasyaInvoiceService service;

    @PostMapping
    public ResponseEntity<SarvasyaInvoice> create(@RequestBody SarvasyaInvoice payload) {
        return ResponseEntity.ok(service.create(payload));
    }

    @GetMapping
    public ResponseEntity<List<SarvasyaInvoice>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SarvasyaInvoice> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SarvasyaInvoice> update(@PathVariable UUID id, @RequestBody SarvasyaInvoice payload) {
        return ResponseEntity.ok(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}








