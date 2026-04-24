package com.sarvasya.sarvasya_lms_backend.model.degree;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "degree")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Degree {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "degree_name", nullable = false)
    private String degreeName;

    @Column(name = "no_of_years", nullable = false)
    private Integer numberOfYears;

    @Column(name = "no_of_semesters", nullable = false)
    private Integer numberOfSemesters;

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








