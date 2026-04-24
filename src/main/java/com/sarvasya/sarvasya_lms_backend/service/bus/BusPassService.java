package com.sarvasya.sarvasya_lms_backend.service.bus;

import java.util.List;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusPassCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusPassResponseDTO;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusPassUpdateRequest;

public interface BusPassService {
    BusPassResponseDTO createBusPass(BusPassCreateRequest request);

    BusPassResponseDTO getBusPassById(UUID id);

    List<BusPassResponseDTO> getAllBusPasses();

    List<BusPassResponseDTO> getBusPassesByUserId(UUID userId);

    List<BusPassResponseDTO> getBusPassesByBusId(UUID busId);

    BusPassResponseDTO updateBusPass(UUID id, BusPassUpdateRequest request);

    void deleteBusPass(UUID id);

}








