package com.sarvasya.sarvasya_lms_backend.repository;

import com.sarvasya.sarvasya_lms_backend.model.AdmitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdmitCardRepository extends JpaRepository<AdmitCard, UUID> {
    List<AdmitCard> findByClassId(UUID classId);
}
