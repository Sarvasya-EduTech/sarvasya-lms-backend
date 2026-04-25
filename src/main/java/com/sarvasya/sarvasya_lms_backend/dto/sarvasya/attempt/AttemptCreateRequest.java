package com.sarvasya.sarvasya_lms_backend.dto.sarvasya.attempt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.common.AssessmentType;

public record AttemptCreateRequest(
        String studentId,
        @NotNull UUID assessmentId,
        @NotNull AssessmentType type,
        BigDecimal score,
        BigDecimal maxScore,
        BigDecimal percentage,
        Boolean isPassed,
        Integer totalQuestions,
        Integer correctAnswers,
        LocalDateTime submittedAt
) {}









