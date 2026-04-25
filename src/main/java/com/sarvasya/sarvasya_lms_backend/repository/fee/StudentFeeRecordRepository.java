package com.sarvasya.sarvasya_lms_backend.repository.fee;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentStatus;
import com.sarvasya.sarvasya_lms_backend.model.fee.StudentFeeRecord;

public interface StudentFeeRecordRepository extends JpaRepository<StudentFeeRecord, UUID> {
    List<StudentFeeRecord> findByStudentId(UUID studentId);
    List<StudentFeeRecord> findByStatus(FeePaymentStatus status);
    List<StudentFeeRecord> findByStudentIdAndStatus(UUID studentId, FeePaymentStatus status);
    Optional<StudentFeeRecord> findByFeeStructureIdAndStudentId(UUID feeStructureId, UUID studentId);
}








