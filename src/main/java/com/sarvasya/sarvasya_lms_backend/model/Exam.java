package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exam {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "batch_id")
    private UUID batchId;

    @Column(name = "total_marks")
    private Integer totalMarks;

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
