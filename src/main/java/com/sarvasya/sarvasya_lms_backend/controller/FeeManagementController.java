package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.model.DegreeDepartmentMapping;
import com.sarvasya.sarvasya_lms_backend.model.FeePaymentMode;
import com.sarvasya.sarvasya_lms_backend.model.FeePaymentStatus;
import com.sarvasya.sarvasya_lms_backend.service.FeeManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/{tenantName}/fees")
@RequiredArgsConstructor
public class FeeManagementController {
    private final FeeManagementService feeManagementService;

    @PostMapping("/structures")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Map<String, Object>> createStructure(
            @PathVariable("tenantName") String tenantName,
            @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(feeManagementService.createStructure(payload));
    }

    @GetMapping("/structures")
    public ResponseEntity<List<Map<String, Object>>> getStructures(
            @PathVariable("tenantName") String tenantName,
            @RequestParam(required = false) UUID degreeId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) Boolean activeOnly
    ) {
        return ResponseEntity.ok(feeManagementService.getStructures(degreeId, departmentId, classId, semester, activeOnly));
    }

    @PutMapping("/structures/{id}")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Map<String, Object>> updateStructure(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(feeManagementService.updateStructure(id, payload));
    }

    @PostMapping("/structures/{id}/opt")
    @PreAuthorize("hasAnyAuthority('user', 'student')")
    public ResponseEntity<Map<String, Object>> optForFees(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(feeManagementService.studentOptAndCreateRecord(id, payload));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'user', 'student')")
    public ResponseEntity<List<Map<String, Object>>> getRecords(
            @PathVariable("tenantName") String tenantName,
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) FeePaymentStatus status
    ) {
        return ResponseEntity.ok(feeManagementService.getRecords(studentId, status));
    }

    @PostMapping("/records/{id}/pay")
    @PreAuthorize("hasAnyAuthority('user', 'student')")
    public ResponseEntity<Map<String, Object>> payOnline(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(feeManagementService.markPaid(id, FeePaymentMode.ONLINE, null));
    }

    @PostMapping("/records/{id}/mark-offline-paid")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Map<String, Object>> markOfflinePaid(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        UUID adminUserId = null;
        if (payload != null && payload.get("adminUserId") != null) {
            adminUserId = UUID.fromString(payload.get("adminUserId").toString());
        }
        return ResponseEntity.ok(feeManagementService.markPaid(id, FeePaymentMode.OFFLINE, adminUserId));
    }

    @GetMapping("/records/{id}/receipt")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'user', 'student')")
    public ResponseEntity<Map<String, Object>> getReceipt(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(feeManagementService.getReceipt(id));
    }

    @GetMapping("/degree-department-mappings")
    public ResponseEntity<List<DegreeDepartmentMapping>> getDegreeDepartmentMappings(
            @PathVariable("tenantName") String tenantName,
            @RequestParam(required = false) UUID degreeId
    ) {
        return ResponseEntity.ok(feeManagementService.getDegreeMappings(degreeId));
    }

    @PostMapping("/degree-department-mappings")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<DegreeDepartmentMapping> createDegreeDepartmentMapping(
            @PathVariable("tenantName") String tenantName,
            @RequestBody Map<String, String> payload
    ) {
        UUID degreeId = UUID.fromString(payload.get("degreeId"));
        UUID departmentId = UUID.fromString(payload.get("departmentId"));
        return ResponseEntity.ok(feeManagementService.createDegreeMapping(degreeId, departmentId));
    }

    @DeleteMapping("/degree-department-mappings")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<Void> deleteDegreeDepartmentMapping(
            @PathVariable("tenantName") String tenantName,
            @RequestParam UUID degreeId,
            @RequestParam UUID departmentId
    ) {
        feeManagementService.deleteDegreeMapping(degreeId, departmentId);
        return ResponseEntity.noContent().build();
    }
}
