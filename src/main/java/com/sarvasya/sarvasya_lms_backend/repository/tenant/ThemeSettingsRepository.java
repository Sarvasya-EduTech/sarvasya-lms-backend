package com.sarvasya.sarvasya_lms_backend.repository.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sarvasya.sarvasya_lms_backend.model.tenant.ThemeSettings;

@Repository
public interface ThemeSettingsRepository extends JpaRepository<ThemeSettings, Long> {
    ThemeSettings findFirstByOrderByIdAsc();
}








