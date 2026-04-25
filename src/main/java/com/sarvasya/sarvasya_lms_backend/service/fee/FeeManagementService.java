package com.sarvasya.sarvasya_lms_backend.service.fee;

import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeOptRequest;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeRecordResponse;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeStructureResponse;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeStructureUpsertRequest;
import com.sarvasya.sarvasya_lms_backend.model.degree.DegreeDepartmentMapping;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentMode;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentStatus;
import java.util.List;
import java.util.UUID;

public interface FeeManagementService {
    FeeStructureResponse createStructure(FeeStructureUpsertRequest payload);
    List<FeeStructureResponse> getStructures(UUID degreeId, UUID departmentId, UUID classId, Integer semester, Boolean activeOnly);
    FeeStructureResponse updateStructure(UUID structureId, FeeStructureUpsertRequest payload);
    FeeRecordResponse studentOptAndCreateRecord(UUID feeStructureId, FeeOptRequest payload);
    List<FeeRecordResponse> getRecords(UUID studentId, FeePaymentStatus status);
    FeeRecordResponse markPaid(UUID recordId, FeePaymentMode mode, UUID adminUserId);
    FeeRecordResponse getReceipt(UUID recordId);
    List<DegreeDepartmentMapping> getDegreeMappings(UUID degreeId);
    DegreeDepartmentMapping createDegreeMapping(UUID degreeId, UUID departmentId);
    void deleteDegreeMapping(UUID degreeId, UUID departmentId);
}
