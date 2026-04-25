package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaPayment;

public interface SarvasyaPaymentService {
    SarvasyaPayment create(SarvasyaPayment sarvasyaPayment);

    List<SarvasyaPayment> findAll();

    Optional<SarvasyaPayment> findById(UUID id);

    SarvasyaPayment update(UUID id, SarvasyaPayment updated);

    void delete(UUID id);

}








