package com.sarvasya.sarvasya_lms_backend.controller.result;

import com.sarvasya.sarvasya_lms_backend.model.result.Result;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.service.result.ResultService;

@RestController
@RequestMapping("/{tenantName}/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService service;

    @GetMapping
    public ResponseEntity<List<Result>> getAll(@PathVariable("tenantName") String tenantName) {
        return ResponseEntity.ok(service.getAllResults());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Result>> getByStudent(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("studentId") UUID studentId) {
        return ResponseEntity.ok(service.getResultsByStudent(studentId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<Result> create(
            @PathVariable("tenantName") String tenantName,
            @RequestBody Result item) {
        return ResponseEntity.ok(service.saveResult(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'professor')")
    public ResponseEntity<Result> update(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody Result item) {
        item.setId(id);
        return ResponseEntity.ok(service.saveResult(item));
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
                    .header("Content-Disposition", "attachment; filename=result_" + id + ".pdf")
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
        service.deleteResult(id);
        return ResponseEntity.ok().build();
    }
}








