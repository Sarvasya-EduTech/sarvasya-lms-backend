package com.sarvasya.sarvasya_lms_backend.model.sarvasya;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "sarvasya_certificate", schema = "tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaCertificate {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "certificate_url", columnDefinition = "TEXT")
    private String certificateUrl;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}








