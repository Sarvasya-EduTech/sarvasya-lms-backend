package com.sarvasya.sarvasya_lms_backend.repository.degree;

import com.sarvasya.sarvasya_lms_backend.model.degree.Degree;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DegreeRepository extends JpaRepository<Degree, UUID> {
}








