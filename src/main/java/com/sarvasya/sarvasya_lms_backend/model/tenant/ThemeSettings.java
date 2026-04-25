package com.sarvasya.sarvasya_lms_backend.model.tenant;

import jakarta.persistence.*;
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
@Entity
@Table(name = "theme_settings")
public class ThemeSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Primary Theme
    @Column(name = "primary_seed_color")
    private String primarySeedColor;
    
    @Column(name = "primary_gradient_start")
    private String primaryGradientStart;
    
    @Column(name = "primary_gradient_end")
    private String primaryGradientEnd;
    
    @Column(name = "primary_gradient_dir")
    private Integer primaryGradientDir;
    
    @Column(name = "primary_use_gradient")
    private Boolean primaryUseGradient;
    
    @Column(name = "primary_text_color")
    private String primaryTextColor;

    // Secondary Theme
    @Column(name = "secondary_background_color")
    private String secondaryBackgroundColor;
    
    @Column(name = "secondary_gradient_start")
    private String secondaryGradientStart;
    
    @Column(name = "secondary_gradient_end")
    private String secondaryGradientEnd;
    
    @Column(name = "secondary_gradient_dir")
    private Integer secondaryGradientDir;
    
    @Column(name = "secondary_use_gradient")
    private Boolean secondaryUseGradient;
    
    @Column(name = "secondary_text_color")
    private String secondaryTextColor;

    // Sidebar Theme
    @Column(name = "sidebar_seed_color")
    private String sidebarSeedColor;
    
    @Column(name = "sidebar_gradient_start")
    private String sidebarGradientStart;
    
    @Column(name = "sidebar_gradient_end")
    private String sidebarGradientEnd;
    
    @Column(name = "sidebar_gradient_dir")
    private Integer sidebarGradientDir;
    
    @Column(name = "sidebar_use_gradient")
    private Boolean sidebarUseGradient;
    
    @Column(name = "sidebar_text_color")
    private String sidebarTextColor;

    // Widget Theme
    @Column(name = "widget_card_background_color")
    private String widgetCardBackgroundColor;

    @Column(name = "widget_card_elevation")
    private Double widgetCardElevation;

    @Column(name = "widget_button_background_color")
    private String widgetButtonBackgroundColor;

    @Column(name = "widget_button_text_color")
    private String widgetButtonTextColor;

    @Column(name = "widget_input_background_color")
    private String widgetInputBackgroundColor;

    @Column(name = "widget_input_border_color")
    private String widgetInputBorderColor;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "logo_version")
    private Long logoVersion;
}








