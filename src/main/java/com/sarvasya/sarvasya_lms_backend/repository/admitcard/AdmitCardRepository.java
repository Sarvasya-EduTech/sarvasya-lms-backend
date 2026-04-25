package com.sarvasya.sarvasya_lms_backend.repository.admitcard;

import com.sarvasya.sarvasya_lms_backend.model.admitcard.AdmitCard;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmitCardRepository extends JpaRepository<AdmitCard, UUID> {
    List<AdmitCard> findByClassId(UUID classId);
}








