package com.sarvasya.sarvasya_lms_backend.model.fee;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "fee_structure")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructure {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "degree_id", nullable = false)
    private UUID degreeId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "class_id")
    private UUID classId;

    @Column(nullable = false)
    private Integer semester;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_by")
    private UUID createdBy;

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








