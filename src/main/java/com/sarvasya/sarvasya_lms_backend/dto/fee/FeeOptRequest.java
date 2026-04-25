package com.sarvasya.sarvasya_lms_backend.dto.fee;

import java.util.List;
import java.util.UUID;

public record FeeOptRequest(
        UUID studentId,
        List<UUID> optionalComponentIds
) {
}








