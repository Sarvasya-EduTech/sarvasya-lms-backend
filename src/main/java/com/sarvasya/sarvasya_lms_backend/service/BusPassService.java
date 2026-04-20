package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.BusPassCreateRequest;
import com.sarvasya.sarvasya_lms_backend.dto.BusPassResponseDTO;
import com.sarvasya.sarvasya_lms_backend.dto.BusPassUpdateRequest;
import com.sarvasya.sarvasya_lms_backend.model.Bus;
import com.sarvasya.sarvasya_lms_backend.model.BusPass;
import com.sarvasya.sarvasya_lms_backend.model.BusStop;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.BusPassRepository;
import com.sarvasya.sarvasya_lms_backend.repository.BusRepository;
import com.sarvasya.sarvasya_lms_backend.repository.BusStopRepository;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BusPassService {

    private final BusPassRepository busPassRepository;
    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final BusStopRepository busStopRepository;

    public BusPassResponseDTO createBusPass(BusPassCreateRequest request) {
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(() -> new IllegalArgumentException("User not found with name: " + request.getUserName()));

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + request.getBusId()));

        BusStop busStop = null;
        if (request.getStopId() != null && !request.getStopId().isEmpty()) {
            busStop = busStopRepository.findById(UUID.fromString(request.getStopId()))
                    .orElseThrow(() -> new IllegalArgumentException("Stop not found with id: " + request.getStopId()));
        }

        BusPass busPass = BusPass.builder()
                .user(user)
                .bus(bus)
                .stop(busStop)
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .build();

        return BusPassResponseDTO.fromEntity(busPassRepository.save(busPass));
    }

    public BusPassResponseDTO getBusPassById(UUID id) {
        return BusPassResponseDTO.fromEntity(busPassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus Pass not found with id: " + id)));
    }

    public List<BusPassResponseDTO> getAllBusPasses() {
        return busPassRepository.findAll().stream()
                .map(BusPassResponseDTO::fromEntity)
                .toList();
    }

    public List<BusPassResponseDTO> getBusPassesByUserId(UUID userId) {
        return busPassRepository.findByUserId(userId).stream()
                .map(BusPassResponseDTO::fromEntity)
                .toList();
    }

    public List<BusPassResponseDTO> getBusPassesByBusId(UUID busId) {
        return busPassRepository.findByBusId(busId).stream()
                .map(BusPassResponseDTO::fromEntity)
                .toList();
    }

    public BusPassResponseDTO updateBusPass(UUID id, BusPassUpdateRequest request) {
        BusPass busPass = busPassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus Pass not found with id: " + id));

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
            busPass.setUser(user);
        }

        if (request.getBusId() != null) {
            Bus bus = busRepository.findById(request.getBusId())
                    .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + request.getBusId()));
            busPass.setBus(bus);
        }

        if (request.getStopId() != null) {
            BusStop busStop = busStopRepository.findById(UUID.fromString(request.getStopId()))
                    .orElseThrow(() -> new IllegalArgumentException("Stop not found with id: " + request.getStopId()));
            busPass.setStop(busStop);
        }

        if (request.getValidFrom() != null)
            busPass.setValidFrom(request.getValidFrom());
        if (request.getValidTo() != null)
            busPass.setValidTo(request.getValidTo());
        if (request.getStatus() != null)
            busPass.setStatus(request.getStatus());

        return BusPassResponseDTO.fromEntity(busPassRepository.save(busPass));
    }

    public void deleteBusPass(UUID id) {
        if (!busPassRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus Pass not found with id: " + id);
        }
        busPassRepository.deleteById(id);
    }
}
