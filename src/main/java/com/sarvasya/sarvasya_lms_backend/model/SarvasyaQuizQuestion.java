package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "sarvasya_quiz_question", schema = "tenant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaQuizQuestion {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "quiz_id")
    private UUID quizId;

    @Column(name = "question_id")
    private UUID questionId;

    @Column(name = "order_index")
    private Integer orderIndex;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
