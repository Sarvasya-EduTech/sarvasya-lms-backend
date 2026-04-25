package com.sarvasya.sarvasya_lms_backend.model.sarvasya;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.sarvasya.sarvasya_lms_backend.model.common.QuestionType;

@Entity
@Table(name = "sarvasya_question", schema = "tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaQuestion {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private BigDecimal marks;

    @Column(name = "negative_marks")
    private BigDecimal negativeMarks;

    @Column(name = "nat_answer")
    private BigDecimal natAnswer;

    @Column(name = "nat_min_range")
    private BigDecimal natMinRange;

    @Column(name = "nat_max_range")
    private BigDecimal natMaxRange;

    @Column(name = "is_range_based")
    @Builder.Default
    private Boolean isRangeBased = false;

    private String topic;

    private String difficulty;

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








