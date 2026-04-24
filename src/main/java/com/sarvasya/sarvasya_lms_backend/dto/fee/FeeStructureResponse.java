package com.sarvasya.sarvasya_lms_backend.dto.fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeeStructureResponse(
        UUID id,
        String title,
        String description,
        UUID degreeId,
        UUID departmentId,
        UUID classId,
        Integer semester,
        LocalDate dueDate,
        boolean isActive,
        UUID createdBy,
        LocalDateTime createdAt,
        List<FeeComponentResponse> components
) {
}








