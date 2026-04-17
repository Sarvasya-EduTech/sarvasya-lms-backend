package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.ThemeSettingsDto;
import com.sarvasya.sarvasya_lms_backend.model.ThemeSettings;
import com.sarvasya.sarvasya_lms_backend.repository.ThemeSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemeSettingsService {

    private final ThemeSettingsRepository repository;

    public ThemeSettingsDto getThemeSettings() {
        List<ThemeSettings> settingsList = repository.findAll();
        if (settingsList.isEmpty()) {
            return getDefaultThemeSettings();
        }
        return mapToDto(settingsList.get(0));
    }

    public ThemeSettingsDto updateThemeSettings(ThemeSettingsDto dto) {
        List<ThemeSettings> settingsList = repository.findAll();
        ThemeSettings entity;
        if (settingsList.isEmpty()) {
            entity = new ThemeSettings();
        } else {
            entity = settingsList.get(0);
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
    }
}
