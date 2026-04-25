package com.sarvasya.sarvasya_lms_backend.model.timetable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "time_table_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableEntry {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_table_id", nullable = false)
    @JsonIgnore
    private TimeTable timeTable;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek; // e.g., "monday"

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrderedEpoch();
        }
    }
}








