package com.sarvasya.sarvasya_lms_backend.service.impl.tenant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.sarvasya.sarvasya_lms_backend.api.NotFoundException;
import com.sarvasya.sarvasya_lms_backend.dto.common.LogoUploadResponse;
import com.sarvasya.sarvasya_lms_backend.dto.tenant.ThemeSettingsDto;
import com.sarvasya.sarvasya_lms_backend.model.tenant.ThemeSettings;
import com.sarvasya.sarvasya_lms_backend.repository.tenant.ThemeSettingsRepository;
import com.sarvasya.sarvasya_lms_backend.service.tenant.ThemeSettingsService;

@Service
@RequiredArgsConstructor
@Transactional
public class ThemeSettingsServiceImpl implements ThemeSettingsService {

    private static final String UPLOAD_DIR = "uploads/logos";

    private final ThemeSettingsRepository repository;

    @Override
    public ThemeSettingsDto getThemeSettings() {
        ThemeSettings entity = repository.findFirstByOrderByIdAsc();
        if (entity == null) {
            return getDefaultThemeSettings();
        }
        return mapToDto(entity);
    }

    @Override
    public ThemeSettingsDto updateThemeSettings(ThemeSettingsDto dto) {
        ThemeSettings entity = repository.findFirstByOrderByIdAsc();
        if (entity == null) {
            ThemeSettingsDto defaults = getDefaultThemeSettings();
            entity = new ThemeSettings();
            mapToEntity(defaults, entity);
        }

        mapToEntity(dto, entity);
        entity = repository.save(entity);
        return mapToDto(entity);
    }

    private ThemeSettingsDto getDefaultThemeSettings() {
        return ThemeSettingsDto.builder()
                .primary(ThemeSettingsDto.PrimaryThemeDto.builder()
                        .seedColor("#009688")
                        .gradientStart("#009688")
                        .gradientEnd("#B2DFDB")
                        .gradientDir(0)
                        .useGradient(false)
                        .textColor("#FFFFFF")
                        .build())
                .secondary(ThemeSettingsDto.SecondaryThemeDto.builder()
                        .backgroundColor("#FFFFFF")
                        .gradientStart("#FFFFFF")
                        .gradientEnd("#FFFFFF")
                        .gradientDir(1)
                        .useGradient(false)
                        .textColor("#1A1A1A")
                        .build())
                .sidebar(ThemeSettingsDto.SidebarThemeDto.builder()
                        .seedColor("#1E1E2C")
                        .gradientStart("#1E1E2C")
                        .gradientEnd("#2D2D44")
                        .gradientDir(1)
                        .useGradient(true)
                        .textColor("#FFFFFF")
                        .build())
                .widgets(ThemeSettingsDto.WidgetThemeDto.builder()
                        .cardBackgroundColor("#FFFFFF")
                        .cardElevation(2.0)
                        .buttonBackgroundColor("#009688")
                        .buttonTextColor("#FFFFFF")
                        .inputBackgroundColor("#FFFFFF")
                        .inputBorderColor("#E0E0E0")
                        .build())
                .logoUrl(null)
                .logoVersion(0L)
                .build();
    }

    private ThemeSettingsDto mapToDto(ThemeSettings entity) {
        return ThemeSettingsDto.builder()
                .primary(ThemeSettingsDto.PrimaryThemeDto.builder()
                        .seedColor(entity.getPrimarySeedColor())
                        .gradientStart(entity.getPrimaryGradientStart())
                        .gradientEnd(entity.getPrimaryGradientEnd())
                        .gradientDir(entity.getPrimaryGradientDir())
                        .useGradient(entity.getPrimaryUseGradient())
                        .textColor(entity.getPrimaryTextColor())
                        .build())
                .secondary(ThemeSettingsDto.SecondaryThemeDto.builder()
                        .backgroundColor(entity.getSecondaryBackgroundColor())
                        .gradientStart(entity.getSecondaryGradientStart())
                        .gradientEnd(entity.getSecondaryGradientEnd())
                        .gradientDir(entity.getSecondaryGradientDir())
                        .useGradient(entity.getSecondaryUseGradient())
                        .textColor(entity.getSecondaryTextColor())
                        .build())
                .sidebar(ThemeSettingsDto.SidebarThemeDto.builder()
                        .seedColor(entity.getSidebarSeedColor())
                        .gradientStart(entity.getSidebarGradientStart())
                        .gradientEnd(entity.getSidebarGradientEnd())
                        .gradientDir(entity.getSidebarGradientDir())
                        .useGradient(entity.getSidebarUseGradient())
                        .textColor(entity.getSidebarTextColor())
                        .build())
                .widgets(ThemeSettingsDto.WidgetThemeDto.builder()
                        .cardBackgroundColor(entity.getWidgetCardBackgroundColor())
                        .cardElevation(entity.getWidgetCardElevation())
                        .buttonBackgroundColor(entity.getWidgetButtonBackgroundColor())
                        .buttonTextColor(entity.getWidgetButtonTextColor())
                        .inputBackgroundColor(entity.getWidgetInputBackgroundColor())
                        .inputBorderColor(entity.getWidgetInputBorderColor())
                        .build())
                .logoUrl(entity.getLogoUrl())
                .logoVersion(entity.getLogoVersion())
                .build();
    }

    private void mapToEntity(ThemeSettingsDto dto, ThemeSettings entity) {
        if (dto.getPrimary() != null) {
            entity.setPrimarySeedColor(dto.getPrimary().getSeedColor());
            entity.setPrimaryGradientStart(dto.getPrimary().getGradientStart());
            entity.setPrimaryGradientEnd(dto.getPrimary().getGradientEnd());
            entity.setPrimaryGradientDir(dto.getPrimary().getGradientDir());
            entity.setPrimaryUseGradient(dto.getPrimary().getUseGradient());
            entity.setPrimaryTextColor(dto.getPrimary().getTextColor());
        }

        if (dto.getSecondary() != null) {
            entity.setSecondaryBackgroundColor(dto.getSecondary().getBackgroundColor());
            entity.setSecondaryGradientStart(dto.getSecondary().getGradientStart());
            entity.setSecondaryGradientEnd(dto.getSecondary().getGradientEnd());
            entity.setSecondaryGradientDir(dto.getSecondary().getGradientDir());
            entity.setSecondaryUseGradient(dto.getSecondary().getUseGradient());
            entity.setSecondaryTextColor(dto.getSecondary().getTextColor());
        }

        if (dto.getSidebar() != null) {
            entity.setSidebarSeedColor(dto.getSidebar().getSeedColor());
            entity.setSidebarGradientStart(dto.getSidebar().getGradientStart());
            entity.setSidebarGradientEnd(dto.getSidebar().getGradientEnd());
            entity.setSidebarGradientDir(dto.getSidebar().getGradientDir());
            entity.setSidebarUseGradient(dto.getSidebar().getUseGradient());
            entity.setSidebarTextColor(dto.getSidebar().getTextColor());
        }

        if (dto.getWidgets() != null) {
            entity.setWidgetCardBackgroundColor(dto.getWidgets().getCardBackgroundColor());
            entity.setWidgetCardElevation(dto.getWidgets().getCardElevation());
            entity.setWidgetButtonBackgroundColor(dto.getWidgets().getButtonBackgroundColor());
            entity.setWidgetButtonTextColor(dto.getWidgets().getButtonTextColor());
            entity.setWidgetInputBackgroundColor(dto.getWidgets().getInputBackgroundColor());
            entity.setWidgetInputBorderColor(dto.getWidgets().getInputBorderColor());
        }

        if (dto.getLogoUrl() != null) {
            entity.setLogoUrl(dto.getLogoUrl());
        }
        if (dto.getLogoVersion() != null) {
            entity.setLogoVersion(dto.getLogoVersion());
        }
    }

    @Override
    public void saveLogo(String logoUrl) {
        ThemeSettings entity = repository.findFirstByOrderByIdAsc();
        if (entity == null) {
            entity = new ThemeSettings();
            ThemeSettingsDto defaults = getDefaultThemeSettings();
            mapToEntity(defaults, entity);
        }
        entity.setLogoUrl(logoUrl);
        entity.setLogoVersion(System.currentTimeMillis());
        repository.save(entity);
    }

    @Override
    public LogoUploadResponse uploadLogo(String tenantId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            Path tenantPath = Paths.get(UPLOAD_DIR).resolve(tenantId);
            if (!Files.exists(tenantPath)) {
                Files.createDirectories(tenantPath);
            }

            String originalName = Objects.requireNonNullElse(file.getOriginalFilename(), "logo.bin");
            String fileName = "logo_" + System.currentTimeMillis() + "_" + originalName.replace("..", "");
            Path filePath = tenantPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String logoUrl = "/sarvasya/tenants/" + tenantId + "/theme/logo/" + fileName;
            saveLogo(logoUrl);
            return new LogoUploadResponse("Logo uploaded successfully", logoUrl);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not upload logo");
        }
    }

    @Override
    public Resource getLogo(String tenantId, String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(tenantId).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new com.sarvasya.sarvasya_lms_backend.api.NotFoundException("Logo not found");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Invalid logo path");
        }
    }

    @Override
    public void deleteLogo() {
        ThemeSettings entity = repository.findFirstByOrderByIdAsc();
        if (entity != null) {
            entity.setLogoUrl(null);
            entity.setLogoVersion(System.currentTimeMillis());
            repository.save(entity);
        }
    }
}









