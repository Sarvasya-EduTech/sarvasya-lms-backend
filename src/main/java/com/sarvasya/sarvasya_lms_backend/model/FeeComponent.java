package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fee_component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeComponent {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "fee_structure_id", nullable = false)
    private UUID feeStructureId;

    @Column(name = "component_name", nullable = false)
    private String componentName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "is_mandatory", nullable = false)
    @JsonProperty("isMandatory")
    private boolean isMandatory;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
