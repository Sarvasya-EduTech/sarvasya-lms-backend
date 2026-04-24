package com.sarvasya.sarvasya_lms_backend.model.sarvasya;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "sarvasya_student_answer", schema = "tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaStudentAnswer {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "attempt_id")
    private UUID attemptId;

    @Column(name = "question_id")
    private UUID questionId;

    @Column(name = "selected_option_ids", columnDefinition = "TEXT")
    private String selectedOptionIds;

    @Column(name = "nat_answer_given")
    private BigDecimal natAnswerGiven;

    @Column(name = "is_correct")
    @Builder.Default
    private Boolean isCorrect = false;

    @Column(name = "marks_awarded")
    private BigDecimal marksAwarded;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}








