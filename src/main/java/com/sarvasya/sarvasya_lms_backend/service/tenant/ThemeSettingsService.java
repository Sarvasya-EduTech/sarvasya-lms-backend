package com.sarvasya.sarvasya_lms_backend.service.tenant;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.sarvasya.sarvasya_lms_backend.dto.common.LogoUploadResponse;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.ThemeSettingsDto;

public interface ThemeSettingsService {
    ThemeSettingsDto getThemeSettings();

    ThemeSettingsDto updateThemeSettings(ThemeSettingsDto dto);

    void saveLogo(String logoUrl);

    LogoUploadResponse uploadLogo(String tenantId, MultipartFile file);

    Resource getLogo(String tenantId, String fileName);

    void deleteLogo();
}








