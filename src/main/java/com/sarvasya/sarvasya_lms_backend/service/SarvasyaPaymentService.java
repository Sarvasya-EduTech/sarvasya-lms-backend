package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.SarvasyaPayment;
import com.sarvasya.sarvasya_lms_backend.repository.SarvasyaPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SarvasyaPaymentService {

    private final SarvasyaPaymentRepository repository;

    @Transactional
    public SarvasyaPayment create(SarvasyaPayment sarvasyaPayment) {
        return repository.save(sarvasyaPayment);
    }

    public List<SarvasyaPayment> findAll() {
        return repository.findAll();
    }

    public Optional<SarvasyaPayment> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public SarvasyaPayment update(UUID id, SarvasyaPayment updated) {
        return repository.findById(id).map(existing -> {
            // TODO: Map specific fields from updated to existing here if needed
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("SarvasyaPayment not found"));
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
