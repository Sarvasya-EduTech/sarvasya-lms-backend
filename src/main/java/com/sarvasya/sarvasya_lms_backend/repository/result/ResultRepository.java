package com.sarvasya.sarvasya_lms_backend.repository.result;

import com.sarvasya.sarvasya_lms_backend.model.result.Result;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends JpaRepository<Result, UUID> {
    List<Result> findByStudentId(UUID studentId);
}








