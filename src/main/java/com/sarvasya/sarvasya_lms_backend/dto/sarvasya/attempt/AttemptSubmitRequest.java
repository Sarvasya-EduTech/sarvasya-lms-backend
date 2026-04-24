package com.sarvasya.sarvasya_lms_backend.dto.sarvasya.attempt;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AttemptSubmitRequest(
        BigDecimal score,
        BigDecimal maxScore,
        BigDecimal percentage,
        Boolean isPassed,
        Integer totalQuestions,
        Integer correctAnswers,
        LocalDateTime submittedAt
) {}









