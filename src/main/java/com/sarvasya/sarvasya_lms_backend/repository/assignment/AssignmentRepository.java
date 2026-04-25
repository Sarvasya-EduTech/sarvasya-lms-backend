package com.sarvasya.sarvasya_lms_backend.repository.assignment;

import com.sarvasya.sarvasya_lms_backend.model.assignment.Assignment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
}








