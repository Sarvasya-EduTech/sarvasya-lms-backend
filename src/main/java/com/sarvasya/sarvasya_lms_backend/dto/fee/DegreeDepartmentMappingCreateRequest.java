package com.sarvasya.sarvasya_lms_backend.dto.fee;

import java.util.UUID;

public record DegreeDepartmentMappingCreateRequest(
        UUID degreeId,
        UUID departmentId
) {
}


