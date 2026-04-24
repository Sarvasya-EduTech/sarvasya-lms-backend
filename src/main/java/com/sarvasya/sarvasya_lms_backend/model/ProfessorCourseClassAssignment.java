package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "professor_course_class_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_professor_class_course_assignment",
                columnNames = {"professor_id", "class_id", "course_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorCourseClassAssignment {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "professor_id", nullable = false)
    private UUID professorId;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

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
