package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaModule;

public interface SarvasyaModuleService {
    SarvasyaModule create(SarvasyaModule sarvasyaModule);

    List<SarvasyaModule> findAll();

    Optional<SarvasyaModule> findById(UUID id);

    SarvasyaModule update(UUID id, SarvasyaModule updated);

    void delete(UUID id);

}








