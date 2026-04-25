package com.sarvasya.sarvasya_lms_backend.dto.fee;

import java.math.BigDecimal;

public record FeeComponentRequest(
        String componentName,
        BigDecimal amount,
        Boolean isMandatory
) {
}


