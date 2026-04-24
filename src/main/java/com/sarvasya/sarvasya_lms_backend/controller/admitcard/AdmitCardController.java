package com.sarvasya.sarvasya_lms_backend.controller.admitcard;

import com.sarvasya.sarvasya_lms_backend.model.admitcard.AdmitCard;
import com.sarvasya.sarvasya_lms_backend.service.admitcard.*;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.service.admitcard.AdmitCardService;

@RestController
@RequestMapping("/{tenantName}/admit-cards")
@RequiredArgsConstructor
public class AdmitCardController {

    private final AdmitCardService service;

    @GetMapping
    public ResponseEntity<List<AdmitCard>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.getAllAdmitCards());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<AdmitCard>> getByClass(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("classId") UUID classId) {
        return ResponseEntity.ok(service.getAdmitCardsByClass(classId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<AdmitCard> create(
            @PathVariable("tenantName") String tenantName,
            @RequestBody AdmitCard item) {
        return ResponseEntity.ok(service.createAdmitCard(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<AdmitCard> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody AdmitCard item) {
        item.setId(id);
        return ResponseEntity.ok(service.createAdmitCard(item));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestParam("userId") UUID userId) {
        try {
            byte[] pdf = service.generatePdf(id, userId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=admit_card.pdf")
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<?> delete(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id) {
        service.deleteAdmitCard(id);
        return ResponseEntity.ok().build();
    }
}
