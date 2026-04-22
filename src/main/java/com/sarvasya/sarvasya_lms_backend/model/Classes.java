package com.sarvasya.sarvasya_lms_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Classes {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String subject;

    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "department_id")
    private UUID departmentId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "class_course",
        joinColumns = @JoinColumn(name = "class_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    @Column(name = "batch_id")
    private UUID batchId;

    @Column(name = "teacher_id")
    private UUID teacherId;

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
