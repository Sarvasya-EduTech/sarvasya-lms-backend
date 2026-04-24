package com.sarvasya.sarvasya_lms_backend.service.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaInvoice;

public interface SarvasyaInvoiceService {
    SarvasyaInvoice create(SarvasyaInvoice sarvasyaInvoice);

    List<SarvasyaInvoice> findAll();

    Optional<SarvasyaInvoice> findById(UUID id);

    SarvasyaInvoice update(UUID id, SarvasyaInvoice updated);

    void delete(UUID id);

}








