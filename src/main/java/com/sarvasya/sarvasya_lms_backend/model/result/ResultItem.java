package com.sarvasya.sarvasya_lms_backend.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "result_items")
public class ResultItem {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Result result;

    @Column(name = "exam_id", nullable = false)
    @JsonProperty("examId")
    private UUID examId;

    @Column(name = "marks_obtained")
    @JsonProperty("marksObtained")
    private Integer marksObtained;

    @Column(name = "total_marks")
    @JsonProperty("totalMarks")
    private Integer totalMarks;
}








