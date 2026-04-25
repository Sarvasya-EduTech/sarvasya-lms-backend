package com.sarvasya.sarvasya_lms_backend.repository.professor;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.professor.ProfessorCourseClassAssignment;

@Repository
public interface ProfessorCourseClassAssignmentRepository extends JpaRepository<ProfessorCourseClassAssignment, UUID> {
    List<ProfessorCourseClassAssignment> findByProfessorId(UUID professorId);
    boolean existsByProfessorIdAndClassIdAndCourseId(UUID professorId, UUID classId, UUID courseId);
}








