package com.sarvasya.sarvasya_lms_backend.dto.fee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentMode;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentStatus;

public record FeeRecordResponse(
        UUID id,
        UUID feeStructureId,
        UUID studentId,
        BigDecimal totalAmount,
        FeePaymentStatus status,
        FeePaymentMode paymentMode,
        String receiptNumber,
        LocalDateTime paidAt,
        UUID offlineMarkedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<FeeComponentResponse> selectedComponents,
        FeeStructureResponse feeStructure
) {
}








