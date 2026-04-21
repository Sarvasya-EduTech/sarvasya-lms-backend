package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sarvasya_payment", schema = "tenant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaPayment {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "course_id")
    private UUID courseId;

    private BigDecimal amount;

    @Column(name = "payment_gateway")
    private String paymentGateway;

    @Column(name = "payment_id")
    private String paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}
