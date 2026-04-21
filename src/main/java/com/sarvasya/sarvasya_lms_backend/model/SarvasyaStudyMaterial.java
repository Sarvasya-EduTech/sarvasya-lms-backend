package com.sarvasya.sarvasya_lms_backend.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sarvasya_study_material", schema = "tenant")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaStudyMaterial {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "module_id")
    private UUID moduleId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private MaterialType type;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_downloadable")
    @Builder.Default
    private Boolean isDownloadable = false;

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
