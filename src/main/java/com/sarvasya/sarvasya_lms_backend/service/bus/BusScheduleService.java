package com.sarvasya.sarvasya_lms_backend.service.bus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusScheduleCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusScheduleResponseDTO;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusScheduleUpdateRequest;

public interface BusScheduleService {
    BusScheduleResponseDTO createBusSchedule(BusScheduleCreateRequest request);

    BusScheduleResponseDTO getBusScheduleById(UUID id);

    List<BusScheduleResponseDTO> getAllBusSchedules();

    List<BusScheduleResponseDTO> getBusSchedulesByBusId(UUID busId);

    BusScheduleResponseDTO updateBusSchedule(UUID id, BusScheduleUpdateRequest request);

    void deleteBusSchedule(UUID id);

}









