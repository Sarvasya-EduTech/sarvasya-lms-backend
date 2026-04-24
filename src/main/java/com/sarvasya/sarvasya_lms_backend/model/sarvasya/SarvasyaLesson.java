package com.sarvasya.sarvasya_lms_backend.model.sarvasya;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "sarvasya_lesson", schema = "tenant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SarvasyaLesson {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "module_id")
    private UUID moduleId;

    @Column(nullable = false)
    private String title;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_preview")
    @Builder.Default
    private Boolean isPreview = false;

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








