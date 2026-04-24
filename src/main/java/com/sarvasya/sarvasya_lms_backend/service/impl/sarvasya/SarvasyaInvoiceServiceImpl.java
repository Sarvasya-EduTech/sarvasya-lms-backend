package com.sarvasya.sarvasya_lms_backend.service.impl.sarvasya;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.model.sarvasya.SarvasyaInvoice;
import com.sarvasya.sarvasya_lms_backend.repository.sarvasya.SarvasyaInvoiceRepository;
import com.sarvasya.sarvasya_lms_backend.service.sarvasya.SarvasyaInvoiceService;

@Service
@RequiredArgsConstructor
public class SarvasyaInvoiceServiceImpl implements SarvasyaInvoiceService {

    private final SarvasyaInvoiceRepository repository;

    @Transactional
    public SarvasyaInvoice create(SarvasyaInvoice sarvasyaInvoice) {
        return repository.save(sarvasyaInvoice);
    }

    public List<SarvasyaInvoice> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaInvoice> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaInvoice update(UUID id, SarvasyaInvoice updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaInvoice not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}








