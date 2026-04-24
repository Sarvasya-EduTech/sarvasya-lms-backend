package com.sarvasya.sarvasya_lms_backend.dto.attendance;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceBulkDeleteRequest {
    private List<UUID> ids;
}








