package com.sarvasya.sarvasya_lms_backend.model.sarvasya;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "sarvasya_quiz", schema = "tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaQuiz {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "module_id")
    private UUID moduleId;

    @Column(name = "passing_score")
    @Builder.Default
    private Integer passingScore = 80;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}








