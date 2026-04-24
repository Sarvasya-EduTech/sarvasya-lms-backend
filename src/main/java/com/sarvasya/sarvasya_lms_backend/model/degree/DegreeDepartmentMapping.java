package com.sarvasya.sarvasya_lms_backend.model.degree;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "degree_department_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DegreeDepartmentMapping {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "degree_id", nullable = false)
    private UUID degreeId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        this.createdAt = LocalDateTime.now();
    }
}








