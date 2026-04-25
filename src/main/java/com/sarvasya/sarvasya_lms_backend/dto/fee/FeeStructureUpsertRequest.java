package com.sarvasya.sarvasya_lms_backend.dto.fee;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FeeStructureUpsertRequest(
        String title,
        String description,
        UUID degreeId,
        UUID departmentId,
        UUID classId,
        Integer semester,
        Boolean isActive,
        UUID createdBy,
        LocalDate dueDate,
        List<FeeComponentRequest> components
) {
}








