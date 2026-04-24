package com.sarvasya.sarvasya_lms_backend.repository.sarvasya;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaCourse;

@Repository
public interface SarvasyaCourseRepository extends JpaRepository<SarvasyaCourse, UUID> {
}








