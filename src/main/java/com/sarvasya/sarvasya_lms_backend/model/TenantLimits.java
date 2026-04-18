package com.sarvasya.sarvasya_lms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenant_limits")
public class TenantLimits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_limit", nullable = false)
    @Builder.Default
    private Long userLimit = 1000L;

    @Column(name = "professor_limit", nullable = false)
    @Builder.Default
    private Long professorLimit = 150L;

    @Column(name = "admin_limit", nullable = false)
    @Builder.Default
    private Long adminLimit = 10L;
}
