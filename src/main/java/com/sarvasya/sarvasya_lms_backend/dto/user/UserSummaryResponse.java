package com.sarvasya.sarvasya_lms_backend.dto.user;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String name,
        String email,
        String role,
        UUID degreeId,
        UUID departmentId,
        UUID classId
) {}


