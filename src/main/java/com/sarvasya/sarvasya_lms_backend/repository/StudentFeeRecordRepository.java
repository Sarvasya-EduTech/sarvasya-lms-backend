package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.FeePaymentStatus;
import com.sarvasya.sarvasya_lms_backend.model.StudentFeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentFeeRecordRepository extends JpaRepository<StudentFeeRecord, UUID> {
    List<StudentFeeRecord> findByStudentId(UUID studentId);
    List<StudentFeeRecord> findByStatus(FeePaymentStatus status);
    List<StudentFeeRecord> findByStudentIdAndStatus(UUID studentId, FeePaymentStatus status);
    Optional<StudentFeeRecord> findByFeeStructureIdAndStudentId(UUID feeStructureId, UUID studentId);
}
