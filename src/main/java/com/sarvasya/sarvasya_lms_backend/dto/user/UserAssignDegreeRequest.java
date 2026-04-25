package com.sarvasya.sarvasya_lms_backend.dto.user;

import java.util.UUID;

public record UserAssignDegreeRequest(
        UUID degreeId,
        UUID departmentId
) {}


