package com.sarvasya.sarvasya_lms_backend.controller.fee;

import com.sarvasya.sarvasya_lms_backend.model.degree.DegreeDepartmentMapping;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.fee.DegreeDepartmentMappingCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeOptRequest;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeRecordResponse;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeStructureResponse;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeStructureUpsertRequest;
import com.sarvasya.sarvasya_lms_backend.dto.fee.OfflinePaymentRequest;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentMode;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentStatus;
import com.sarvasya.sarvasya_lms_backend.service.fee.FeeManagementService;

@RestController
@RequestMapping("/{tenantName}/fees")
@RequiredArgsConstructor
public class FeeManagementController {
    private final FeeManagementService feeManagementService;

    @PostMapping("/structures")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<FeeStructureResponse> createStructure(
            @PathVariable("tenantName") String tenantName,
            @RequestBody FeeStructureUpsertRequest payload
    ) {
        return ResponseEntity.ok(feeManagementService.createStructure(payload));
    }

    @GetMapping("/structures")
    public ResponseEntity<List<FeeStructureResponse>> getStructures(
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
    public ResponseEntity<FeeStructureResponse> updateStructure(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody FeeStructureUpsertRequest payload
    ) {
        return ResponseEntity.ok(feeManagementService.updateStructure(id, payload));
    }

    @PostMapping("/structures/{id}/opt")
    @PreAuthorize("hasAnyAuthority('user', 'student')")
    public ResponseEntity<FeeRecordResponse> optForFees(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody FeeOptRequest payload
    ) {
        return ResponseEntity.ok(feeManagementService.studentOptAndCreateRecord(id, payload));
    }

    @GetMapping("/records")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'user', 'student')")
    public ResponseEntity<List<FeeRecordResponse>> getRecords(
            @PathVariable("tenantName") String tenantName,
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) FeePaymentStatus status
    ) {
        return ResponseEntity.ok(feeManagementService.getRecords(studentId, status));
    }

    @PostMapping("/records/{id}/pay")
    @PreAuthorize("hasAnyAuthority('user', 'student')")
    public ResponseEntity<FeeRecordResponse> payOnline(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(feeManagementService.markPaid(id, FeePaymentMode.ONLINE, null));
    }

    @PostMapping("/records/{id}/mark-offline-paid")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin')")
    public ResponseEntity<FeeRecordResponse> markOfflinePaid(
            @PathVariable("tenantName") String tenantName,
            @PathVariable("id") UUID id,
            @RequestBody(required = false) OfflinePaymentRequest payload
    ) {
        UUID adminUserId = payload == null ? null : payload.adminUserId();
        return ResponseEntity.ok(feeManagementService.markPaid(id, FeePaymentMode.OFFLINE, adminUserId));
    }

    @GetMapping("/records/{id}/receipt")
    @PreAuthorize("hasAnyAuthority('sarvasya-admin', 'admin', 'user', 'student')")
    public ResponseEntity<FeeRecordResponse> getReceipt(
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
            @RequestBody DegreeDepartmentMappingCreateRequest payload
    ) {
        return ResponseEntity.ok(feeManagementService.createDegreeMapping(payload.degreeId(), payload.departmentId()));
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








