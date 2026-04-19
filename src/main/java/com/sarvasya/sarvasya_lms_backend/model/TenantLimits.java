package com.sarvasya.sarvasya_lms_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantLimits {

    @Builder.Default
    private Long userLimit = 1000L;

    @Builder.Default
    private Long professorLimit = 150L;

    @Builder.Default
    private Long adminLimit = 10L;
}
