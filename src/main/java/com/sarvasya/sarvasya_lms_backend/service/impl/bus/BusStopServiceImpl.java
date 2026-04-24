package com.sarvasya.sarvasya_lms_backend.service.impl.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.Bus;
import com.sarvasya.sarvasya_lms_backend.model.bus.BusStop;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusStopRequest;
import com.sarvasya.sarvasya_lms_backend.dto.bus.BusStopResponseDTO;
import com.sarvasya.sarvasya_lms_backend.repository.bus.BusRepository;
import com.sarvasya.sarvasya_lms_backend.repository.bus.BusStopRepository;
import com.sarvasya.sarvasya_lms_backend.service.bus.BusStopService;

@Service
@RequiredArgsConstructor
@Transactional
public class BusStopServiceImpl implements BusStopService {

    private final BusStopRepository busStopRepository;
    private final BusRepository busRepository;

    public BusStopResponseDTO createBusStop(BusStopRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + request.getBusId()));

        BusStop stop = BusStop.builder()
                .bus(bus)
                .stopName(request.getStopName())
                .build();

        return BusStopResponseDTO.fromEntity(busStopRepository.save(stop));
    }

    public BusStopResponseDTO getBusStopById(UUID id) {
        return BusStopResponseDTO.fromEntity(busStopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus stop not found with id: " + id)));
    }

    public List<BusStopResponseDTO> getStopsByBusId(UUID busId) {
        return busStopRepository.findByBusId(busId).stream()
                .map(BusStopResponseDTO::fromEntity)
                .toList();
    }

    public BusStopResponseDTO updateBusStop(UUID id, BusStopRequest request) {
        BusStop stop = busStopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus stop not found with id: " + id));

        if (request.getStopName() != null) {
            stop.setStopName(request.getStopName());
        }

        return BusStopResponseDTO.fromEntity(busStopRepository.save(stop));
    }

    public void deleteBusStop(UUID id) {
        if (!busStopRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus stop not found with id: " + id);
        }
        busStopRepository.deleteById(id);
    }
}








