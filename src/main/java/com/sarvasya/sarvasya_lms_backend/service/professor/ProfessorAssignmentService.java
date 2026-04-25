package com.sarvasya.sarvasya_lms_backend.service.professor;

import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.dto.professor.ProfessorAssignmentRequest;
import com.sarvasya.sarvasya_lms_backend.model.professor.ProfessorCourseClassAssignment;

public interface ProfessorAssignmentService {
    List<ProfessorCourseClassAssignment> getAll();

    List<ProfessorCourseClassAssignment> getByProfessorId(UUID professorId);

    boolean isProfessorAssigned(UUID professorId, UUID classId, UUID courseId);

    List<ProfessorCourseClassAssignment> createBulk(ProfessorAssignmentRequest request);

    void delete(UUID id);

    void bulkDelete(List<UUID> ids);
}








