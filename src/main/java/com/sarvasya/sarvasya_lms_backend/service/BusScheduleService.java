package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.BusScheduleCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.BusScheduleResponseDTO;
import com.sarvasya.sarvasya_lms_backend.dto.BusScheduleUpdateRequest;
import com.sarvasya.sarvasya_lms_backend.model.Bus;
import com.sarvasya.sarvasya_lms_backend.model.BusSchedule;
import com.sarvasya.sarvasya_lms_backend.model.BusStop;
import com.sarvasya.sarvasya_lms_backend.model.BusScheduleStop;
import com.sarvasya.sarvasya_lms_backend.repository.BusScheduleRepository;
import com.sarvasya.sarvasya_lms_backend.repository.BusRepository;
import com.sarvasya.sarvasya_lms_backend.repository.BusStopRepository;
import com.sarvasya.sarvasya_lms_backend.repository.BusScheduleStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BusScheduleService {

    private final BusScheduleRepository busScheduleRepository;
    private final BusRepository busRepository;
    private final BusStopRepository busStopRepository;
    private final BusScheduleStopRepository busScheduleStopRepository;

    public BusScheduleResponseDTO createBusSchedule(BusScheduleCreateRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + request.getBusId()));

        BusSchedule schedule = BusSchedule.builder()
                .bus(bus)
                .routeName(request.getRouteName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        BusSchedule savedSchedule = busScheduleRepository.save(schedule);

        if (request.getStops() != null) {
            List<BusScheduleStop> scheduleStops = new ArrayList<>();
            for (int i = 0; i < request.getStops().size(); i++) {
                BusScheduleCreateRequest.RouteStopRequest stopReq = request.getStops().get(i);
                
                BusStop busStop;
                if (stopReq.getId() != null && !stopReq.getId().isEmpty()) {
                    busStop = busStopRepository.findById(UUID.fromString(stopReq.getId()))
                            .orElseThrow(() -> new IllegalArgumentException("Stop not found with id: " + stopReq.getId()));
                } else {
                    // Try to find existing stop by name for this bus, or create new
                    busStop = busStopRepository.findByBusIdAndStopName(bus.getId(), stopReq.getStopName())
                            .orElseGet(() -> busStopRepository.save(BusStop.builder()
                                    .bus(bus)
                                    .stopName(stopReq.getStopName())
                                    .build()));
                }

                scheduleStops.add(BusScheduleStop.builder()
                        .schedule(savedSchedule)
                        .stop(busStop)
                        .arrivalTime(stopReq.getArrivalTime())
                        .sequenceNumber(i)
                        .build());
            }
            busScheduleStopRepository.saveAll(scheduleStops);
            savedSchedule.getScheduleStops().addAll(scheduleStops);
        }

        return BusScheduleResponseDTO.fromEntity(savedSchedule);
    }

    public BusScheduleResponseDTO getBusScheduleById(UUID id) {
        return BusScheduleResponseDTO.fromEntity(busScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus Schedule not found with id: " + id)));
    }

    public List<BusScheduleResponseDTO> getAllBusSchedules() {
        return busScheduleRepository.findAll().stream()
                .map(BusScheduleResponseDTO::fromEntity)
                .toList();
    }

    public List<BusScheduleResponseDTO> getBusSchedulesByBusId(UUID busId) {
        return busScheduleRepository.findByBusId(busId).stream()
                .map(BusScheduleResponseDTO::fromEntity)
                .toList();
    }

    public BusScheduleResponseDTO updateBusSchedule(UUID id, BusScheduleUpdateRequest request) {
        BusSchedule schedule = busScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus Schedule not found with id: " + id));
        
        if (request.getBusId() != null) {
            Bus bus = busRepository.findById(request.getBusId())
                    .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + request.getBusId()));
            schedule.setBus(bus);
        }
        
        if (request.getRouteName() != null) schedule.setRouteName(request.getRouteName());
        if (request.getStartTime() != null) schedule.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) schedule.setEndTime(request.getEndTime());
        
        if (request.getStops() != null) {
            // clear() triggers orphanRemoval — no need to call deleteByScheduleId
            schedule.getScheduleStops().clear();
            busScheduleRepository.saveAndFlush(schedule); // flush deletes before re-inserting

            List<BusScheduleStop> scheduleStops = new ArrayList<>();
            for (int i = 0; i < request.getStops().size(); i++) {
                BusScheduleUpdateRequest.RouteStopRequest stopReq = request.getStops().get(i);

                BusStop busStop;
                if (stopReq.getId() != null && !stopReq.getId().isEmpty()) {
                    busStop = busStopRepository.findById(UUID.fromString(stopReq.getId()))
                            .orElseThrow(() -> new IllegalArgumentException("Stop not found with id: " + stopReq.getId()));
                } else {
                    busStop = busStopRepository.findByBusIdAndStopName(schedule.getBus().getId(), stopReq.getStopName())
                            .orElseGet(() -> busStopRepository.save(BusStop.builder()
                                    .bus(schedule.getBus())
                                    .stopName(stopReq.getStopName())
                                    .build()));
                }

                scheduleStops.add(BusScheduleStop.builder()
                        .schedule(schedule)
                        .stop(busStop)
                        .arrivalTime(stopReq.getArrivalTime())
                        .sequenceNumber(i)
                        .build());
            }
            schedule.getScheduleStops().addAll(scheduleStops);
        }
        
        return BusScheduleResponseDTO.fromEntity(busScheduleRepository.save(schedule));
    }

    public void deleteBusSchedule(UUID id) {
        if (!busScheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus Schedule not found with id: " + id);
        }
        busScheduleRepository.deleteById(id);
    }
}
