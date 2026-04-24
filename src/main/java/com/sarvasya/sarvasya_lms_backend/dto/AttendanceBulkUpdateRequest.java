package com.sarvasya.sarvasya_lms_backend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AttendanceBulkUpdateRequest {
    private List<UUID> sessionIds;
    private String notes;
}
