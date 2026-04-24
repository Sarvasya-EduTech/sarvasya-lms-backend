package com.sarvasya.sarvasya_lms_backend.service.bus;

import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusStopRequest;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusStopResponseDTO;

public interface BusStopService {
    BusStopResponseDTO createBusStop(BusStopRequest request);

    BusStopResponseDTO getBusStopById(UUID id);

    List<BusStopResponseDTO> getStopsByBusId(UUID busId);

    BusStopResponseDTO updateBusStop(UUID id, BusStopRequest request);

    void deleteBusStop(UUID id);

}








