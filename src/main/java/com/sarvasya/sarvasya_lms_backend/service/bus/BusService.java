package com.sarvasya.sarvasya_lms_backend.service.bus;

import com.sarvasya.sarvasya_lms_backend.model.bus.Bus;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BusService {
    Bus createBus(Bus bus);

    Bus getBusById(UUID id);

    List<Bus> getAllBuses();

    Bus updateBus(UUID id, Bus busDetails);

    void deleteBus(UUID id);

}








