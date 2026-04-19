package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.BusScheduleResponseDTO;
import com.sarvasya.sarvasya_lms_backend.model.BusSchedule;
import com.sarvasya.sarvasya_lms_backend.repository.BusScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BusScheduleService {

    private final BusScheduleRepository busScheduleRepository;

    public BusScheduleResponseDTO createBusSchedule(BusSchedule busSchedule) {
        return BusScheduleResponseDTO.fromEntity(busScheduleRepository.save(busSchedule));
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

    public BusScheduleResponseDTO updateBusSchedule(UUID id, BusSchedule scheduleDetails) {
        BusSchedule schedule = busScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus Schedule not found with id: " + id));
        schedule.setBus(scheduleDetails.getBus());
        schedule.setRouteName(scheduleDetails.getRouteName());
        schedule.setStartTime(scheduleDetails.getStartTime());
        schedule.setEndTime(scheduleDetails.getEndTime());
        return BusScheduleResponseDTO.fromEntity(busScheduleRepository.save(schedule));
    }

    public void deleteBusSchedule(UUID id) {
        if (!busScheduleRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus Schedule not found with id: " + id);
        }
        busScheduleRepository.deleteById(id);
    }
}
