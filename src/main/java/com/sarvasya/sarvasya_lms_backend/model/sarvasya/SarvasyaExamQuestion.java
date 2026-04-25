package com.sarvasya.sarvasya_lms_backend.model.sarvasya;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "sarvasya_exam_question", schema = "tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaExamQuestion {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "exam_id")
    private UUID examId;

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








