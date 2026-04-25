package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaStudyMaterial;

public interface SarvasyaStudyMaterialService {
    SarvasyaStudyMaterial create(SarvasyaStudyMaterial sarvasyaStudyMaterial);

    List<SarvasyaStudyMaterial> findAll();

    Optional<SarvasyaStudyMaterial> findById(UUID id);

    SarvasyaStudyMaterial update(UUID id, SarvasyaStudyMaterial updated);

    void delete(UUID id);

}








