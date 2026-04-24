package com.sarvasya.sarvasya_lms_backend.dto.professor;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorAssignmentRequest {
    private UUID professorId;
    private List<UUID> classIds;
    private List<UUID> courseIds;
}








