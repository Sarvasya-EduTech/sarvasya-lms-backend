package com.sarvasya.sarvasya_lms_backend.dto.fee;

import java.math.BigDecimal;
import java.util.UUID;

public record FeeComponentResponse(
        UUID id,
        UUID feeStructureId,
        String componentName,
        BigDecimal amount,
        boolean isMandatory
) {
}








