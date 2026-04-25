package com.sarvasya.sarvasya_lms_backend.service.impl.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.Bus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.repository.bus.BusRepository;
import com.sarvasya.sarvasya_lms_backend.service.bus.BusService;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {

    private static final Logger logger = LoggerFactory.getLogger(BusService.class);
    private final BusRepository busRepository;

    public Bus createBus(Bus bus) {
        logger.info("Creating bus with busNumber: {}, capacity: {}", bus.getBusNumber(), bus.getCapacity());
        Bus savedBus = busRepository.save(bus);
        logger.info("Bus saved successfully with ID: {}", savedBus.getId());
        return savedBus;
    }

    public Bus getBusById(UUID id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + id));
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus updateBus(UUID id, Bus busDetails) {
        Bus bus = getBusById(id);
        bus.setBusNumber(busDetails.getBusNumber());
        bus.setCapacity(busDetails.getCapacity());
        return busRepository.save(bus);
    }

    public void deleteBus(UUID id) {
        if (!busRepository.existsById(id)) {
            throw new IllegalArgumentException("Bus not found with id: " + id);
        }
        busRepository.deleteById(id);
    }
}








