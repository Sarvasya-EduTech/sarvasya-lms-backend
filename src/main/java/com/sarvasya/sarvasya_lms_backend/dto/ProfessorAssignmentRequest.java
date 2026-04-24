package com.sarvasya.sarvasya_lms_backend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProfessorAssignmentRequest {
    private UUID professorId;
    private List<UUID> classIds;
    private List<UUID> courseIds;
}
