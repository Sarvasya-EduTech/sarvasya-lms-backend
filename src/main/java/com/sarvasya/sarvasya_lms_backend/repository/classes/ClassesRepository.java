package com.sarvasya.sarvasya_lms_backend.repository.classes;

import com.sarvasya.sarvasya_lms_backend.model.classes.Classes;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassesRepository extends JpaRepository<Classes, UUID> {
}








