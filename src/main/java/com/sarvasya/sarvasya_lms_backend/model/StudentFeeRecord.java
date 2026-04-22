package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "student_fee_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeRecord {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "fee_structure_id", nullable = false)
    private UUID feeStructureId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "selected_component_ids")
    private String selectedComponentIds;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeePaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode")
    private FeePaymentMode paymentMode;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "offline_marked_by")
    private UUID offlineMarkedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        if (this.status == null) {
            this.status = FeePaymentStatus.UNPAID;
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
