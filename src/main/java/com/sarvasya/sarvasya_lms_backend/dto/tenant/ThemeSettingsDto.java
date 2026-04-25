package com.sarvasya.sarvasya_lms_backend.dto.tenant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeSettingsDto {
    private PrimaryThemeDto primary;
    private SecondaryThemeDto secondary;
    private SidebarThemeDto sidebar;
    private WidgetThemeDto widgets;
    private String logoUrl;
    private Long logoVersion;
    private String tenantId;
    
    @Getter
@Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrimaryThemeDto {
        private String seedColor;
        private String gradientStart;
        private String gradientEnd;
        private Integer gradientDir;
        private Boolean useGradient;
        private String textColor;
    }

    @Getter
@Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecondaryThemeDto {
        private String backgroundColor;
        private String gradientStart;
        private String gradientEnd;
        private Integer gradientDir;
        private Boolean useGradient;
        private String textColor;
    }

    @Getter
@Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SidebarThemeDto {
        private String seedColor;
        private String gradientStart;
        private String gradientEnd;
        private Integer gradientDir;
        private Boolean useGradient;
        private String textColor;
    }

    @Getter
@Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WidgetThemeDto {
        private String cardBackgroundColor;
        private Double cardElevation;
        private String buttonBackgroundColor;
        private String buttonTextColor;
        private String inputBackgroundColor;
        private String inputBorderColor;
    }
}








