package com.sarvasya.sarvasya_lms_backend.dto.sarvasya.attempt;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.common.AssessmentType;

public record AttemptStartRequest(
        String studentId,
        @NotNull UUID assessmentId,
        @NotNull AssessmentType type
) {}









