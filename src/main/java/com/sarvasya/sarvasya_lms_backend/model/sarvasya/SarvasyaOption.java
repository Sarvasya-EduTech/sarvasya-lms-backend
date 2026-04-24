package com.sarvasya.sarvasya_lms_backend.model.sarvasya;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "sarvasya_option", schema = "tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaOption {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "question_id")
    private UUID questionId;

    @Column(nullable = false)
    private String text;

    @Column(name = "is_correct")
    @Builder.Default
    private Boolean isCorrect = false;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}








