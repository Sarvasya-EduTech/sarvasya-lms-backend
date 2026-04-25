package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaOption;

public interface SarvasyaOptionService {
    SarvasyaOption create(SarvasyaOption sarvasyaOption);

    List<SarvasyaOption> findAll();

    Optional<SarvasyaOption> findById(UUID id);

    SarvasyaOption update(UUID id, SarvasyaOption updated);

    void delete(UUID id);

}








