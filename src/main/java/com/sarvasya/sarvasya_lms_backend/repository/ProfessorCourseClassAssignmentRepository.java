package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.ProfessorCourseClassAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfessorCourseClassAssignmentRepository extends JpaRepository<ProfessorCourseClassAssignment, UUID> {
    List<ProfessorCourseClassAssignment> findByProfessorId(UUID professorId);
    boolean existsByProfessorIdAndClassIdAndCourseId(UUID professorId, UUID classId, UUID courseId);
}
