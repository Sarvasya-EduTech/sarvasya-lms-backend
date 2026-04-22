package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sarvasya_attempt", schema = "tenant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaAttempt {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "assessment_id")
    private UUID assessmentId;

    @Enumerated(EnumType.STRING)
    private AssessmentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private AttemptStatus status = AttemptStatus.SUBMITTED;

    private BigDecimal score;

    @Column(name = "max_score")
    private BigDecimal maxScore;

    private BigDecimal percentage;

    @Column(name = "is_passed")
    @Builder.Default
    private Boolean isPassed = false;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
