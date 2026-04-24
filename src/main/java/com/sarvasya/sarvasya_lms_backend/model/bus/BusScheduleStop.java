package com.sarvasya.sarvasya_lms_backend.model.bus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import java.time.LocalTime;
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
@Table(name = "bus_schedule_stops")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BusScheduleStop {

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
    @JoinColumn(name = "schedule_id", nullable = false)
    private BusSchedule schedule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stop_id", nullable = false)
    private BusStop stop;

    @Column(name = "arrival_time", nullable = false)
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime arrivalTime;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;
}








