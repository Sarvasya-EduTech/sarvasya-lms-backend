package com.sarvasya.sarvasya_lms_backend.model.admitcard;

import com.sarvasya.sarvasya_lms_backend.model.exam.Exam;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "admit_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdmitCard {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "admit_card_exam",
        joinColumns = @JoinColumn(name = "admit_card_id"),
        inverseJoinColumns = @JoinColumn(name = "exam_id"))
    private Set<Exam> exams = new HashSet<>();

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









