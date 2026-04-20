package com.sarvasya.sarvasya_lms_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "calendar_item", indexes = {
    @Index(name = "idx_calendar_item_start_datetime", columnList = "start_date_time"),
    @Index(name = "idx_calendar_item_type", columnList = "type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarItem {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalendarItemType type;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "all_day", nullable = false)
    private Boolean allDay = false;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    private ReferenceType referenceType;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validateReferences();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        validateReferences();
    }

    private void validateReferences() {
        if (type == CalendarItemType.CLASS) {
            if (referenceType != ReferenceType.CLASS || referenceId == null) {
                throw new IllegalStateException("CalendarItem of type CLASS must have referenceType CLASS and a non-null referenceId");
            }
        } else if (type == CalendarItemType.EXAM) {
            if (referenceType != ReferenceType.EXAM || referenceId == null) {
                throw new IllegalStateException("CalendarItem of type EXAM must have referenceType EXAM and a non-null referenceId");
            }
        } else {
            if (referenceType != null || referenceId != null) {
                throw new IllegalStateException("CalendarItem of type " + type.name() + " must have null referenceType and referenceId");
            }
        }
    }
}
