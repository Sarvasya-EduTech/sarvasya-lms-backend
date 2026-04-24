package com.sarvasya.sarvasya_lms_backend.model.attendance;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "attendance_sessions",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_attendance_session_per_day",
                columnNames = {"class_id", "course_id", "attendance_date"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSession {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "professor_id", nullable = false)
    private UUID professorId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "attendanceSession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AttendanceRecord> records = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}








