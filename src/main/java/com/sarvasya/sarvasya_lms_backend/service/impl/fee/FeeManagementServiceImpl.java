package com.sarvasya.sarvasya_lms_backend.service.impl.fee;

import com.sarvasya.sarvasya_lms_backend.model.degree.DegreeDepartmentMapping;
import com.sarvasya.sarvasya_lms_backend.model.department.Department;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.api.NotFoundException;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeComponentRequest;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeComponentResponse;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeOptRequest;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeRecordResponse;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeStructureResponse;
import com.sarvasya.sarvasya_lms_backend.dto.fee.FeeStructureUpsertRequest;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeeComponent;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentMode;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeePaymentStatus;
import com.sarvasya.sarvasya_lms_backend.model.fee.FeeStructure;
import com.sarvasya.sarvasya_lms_backend.model.fee.StudentFeeRecord;
import com.sarvasya.sarvasya_lms_backend.repository.degree.DegreeDepartmentMappingRepository;
import com.sarvasya.sarvasya_lms_backend.repository.fee.FeeComponentRepository;
import com.sarvasya.sarvasya_lms_backend.repository.fee.FeeStructureRepository;
import com.sarvasya.sarvasya_lms_backend.repository.fee.StudentFeeRecordRepository;
import com.sarvasya.sarvasya_lms_backend.service.fee.FeeManagementService;

@Service
@RequiredArgsConstructor
public class FeeManagementServiceImpl implements FeeManagementService {
    private final FeeStructureRepository feeStructureRepository;
    private final FeeComponentRepository feeComponentRepository;
    private final StudentFeeRecordRepository studentFeeRecordRepository;
    private final DegreeDepartmentMappingRepository degreeDepartmentMappingRepository;

    @Override
    @Transactional
    public FeeStructureResponse createStructure(FeeStructureUpsertRequest payload) {
        validateStructurePayload(payload);

        if (!degreeDepartmentMappingRepository.existsByDegreeIdAndDepartmentId(payload.degreeId(), payload.departmentId())) {
            throw new IllegalArgumentException("Department is not mapped to selected degree");
        }

        FeeStructure structure = new FeeStructure();
        structure.setTitle(payload.title());
        structure.setDescription(payload.description());
        structure.setDegreeId(payload.degreeId());
        structure.setDepartmentId(payload.departmentId());
        structure.setClassId(payload.classId());
        structure.setSemester(payload.semester());
        structure.setActive(payload.isActive() == null || payload.isActive());
        structure.setCreatedBy(payload.createdBy());
        structure.setDueDate(payload.dueDate());
        FeeStructure savedStructure = feeStructureRepository.save(structure);

        List<FeeComponent> savedComponents = saveComponents(savedStructure.getId(), payload.components());
        return toStructureResponse(savedStructure, savedComponents);
    }

    @Override
    @Transactional
    public FeeStructureResponse updateStructure(UUID structureId, FeeStructureUpsertRequest payload) {
        validateStructurePayload(payload);
        FeeStructure structure = feeStructureRepository.findById(structureId)
                .orElseThrow(() -> new NotFoundException("Fee structure not found"));

        if (!degreeDepartmentMappingRepository.existsByDegreeIdAndDepartmentId(payload.degreeId(), payload.departmentId())) {
            throw new IllegalArgumentException("Department is not mapped to selected degree");
        }

        structure.setTitle(payload.title());
        structure.setDescription(payload.description());
        structure.setDegreeId(payload.degreeId());
        structure.setDepartmentId(payload.departmentId());
        structure.setClassId(payload.classId());
        structure.setSemester(payload.semester());
        structure.setActive(payload.isActive() == null || payload.isActive());
        structure.setDueDate(payload.dueDate());
        FeeStructure savedStructure = feeStructureRepository.save(structure);

        feeComponentRepository.deleteByFeeStructureId(savedStructure.getId());
        List<FeeComponent> savedComponents = saveComponents(savedStructure.getId(), payload.components());
        return toStructureResponse(savedStructure, savedComponents);
    }

    @Override
    public List<FeeStructureResponse> getStructures(UUID degreeId, UUID departmentId, UUID classId, Integer semester, Boolean activeOnly) {
        List<FeeStructure> structures;
        if (classId != null) {
            structures = activeOnly != null
                    ? feeStructureRepository.findByClassIdAndIsActive(classId, activeOnly)
                    : feeStructureRepository.findByClassId(classId);
            if (semester != null) {
                structures = structures.stream().filter(s -> Objects.equals(s.getSemester(), semester)).toList();
            }
        } else if (degreeId != null && departmentId != null && semester != null) {
            structures = feeStructureRepository.findByDegreeIdAndDepartmentIdAndSemesterAndIsActive(
                    degreeId, departmentId, semester, activeOnly == null || activeOnly
            );
        } else if (degreeId != null && departmentId != null) {
            structures = feeStructureRepository.findAll().stream()
                    .filter(s -> s.getDegreeId().equals(degreeId) && s.getDepartmentId().equals(departmentId))
                    .filter(s -> activeOnly == null || s.isActive() == activeOnly)
                    .collect(Collectors.toList());
        } else if (activeOnly != null) {
            structures = feeStructureRepository.findByIsActive(activeOnly);
        } else {
            structures = feeStructureRepository.findAll();
        }

        return structures.stream()
                .map(s -> toStructureResponse(s, feeComponentRepository.findByFeeStructureId(s.getId())))
                .toList();
    }

    @Override
    @Transactional
    public FeeRecordResponse studentOptAndCreateRecord(UUID feeStructureId, FeeOptRequest payload) {
        if (payload == null || payload.studentId() == null) {
            throw new IllegalArgumentException("studentId is required");
        }

        FeeStructure structure = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new NotFoundException("Fee structure not found"));

        List<FeeComponent> components = feeComponentRepository.findByFeeStructureId(feeStructureId);
        Set<UUID> chosenOptionalIds = payload.optionalComponentIds() == null
                ? Set.of()
                : new HashSet<>(payload.optionalComponentIds());

        List<FeeComponent> selected = components.stream()
                .filter(c -> c.isMandatory() || chosenOptionalIds.contains(c.getId()))
                .toList();

        BigDecimal total = selected.stream()
                .map(FeeComponent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StudentFeeRecord record = studentFeeRecordRepository.findByFeeStructureIdAndStudentId(feeStructureId, payload.studentId())
                .orElseGet(StudentFeeRecord::new);
        record.setFeeStructureId(feeStructureId);
        record.setStudentId(payload.studentId());
        record.setSelectedComponentIds(selected.stream().map(c -> c.getId().toString()).collect(Collectors.joining(",")));
        record.setTotalAmount(total);
        if (record.getStatus() == null) {
            record.setStatus(FeePaymentStatus.UNPAID);
        }
        StudentFeeRecord saved = studentFeeRecordRepository.save(record);
        return toRecordResponse(saved, structure, components);
    }

    @Override
    @Transactional
    public FeeRecordResponse markPaid(UUID recordId, FeePaymentMode mode, UUID adminUserId) {
        StudentFeeRecord record = studentFeeRecordRepository.findById(recordId)
                .orElseThrow(() -> new NotFoundException("Record not found"));
        record.setStatus(FeePaymentStatus.PAID);
        record.setPaymentMode(mode);
        record.setPaidAt(LocalDateTime.now());
        if (mode == FeePaymentMode.OFFLINE) {
            record.setOfflineMarkedBy(adminUserId);
        }
        if (record.getReceiptNumber() == null || record.getReceiptNumber().isBlank()) {
            record.setReceiptNumber("RCPT-" + record.getId().toString().substring(0, 8).toUpperCase() + "-" + System.currentTimeMillis());
        }
        StudentFeeRecord saved = studentFeeRecordRepository.save(record);
        FeeStructure structure = feeStructureRepository.findById(saved.getFeeStructureId()).orElse(null);
        List<FeeComponent> components = feeComponentRepository.findByFeeStructureId(saved.getFeeStructureId());
        return toRecordResponse(saved, structure, components);
    }

    @Override
    public List<FeeRecordResponse> getRecords(UUID studentId, FeePaymentStatus status) {
        List<StudentFeeRecord> records;
        if (studentId != null && status != null) {
            records = studentFeeRecordRepository.findByStudentIdAndStatus(studentId, status);
        } else if (studentId != null) {
            records = studentFeeRecordRepository.findByStudentId(studentId);
        } else if (status != null) {
            records = studentFeeRecordRepository.findByStatus(status);
        } else {
            records = studentFeeRecordRepository.findAll();
        }

        return records.stream().map(record -> {
            FeeStructure structure = feeStructureRepository.findById(record.getFeeStructureId()).orElse(null);
            List<FeeComponent> components = feeComponentRepository.findByFeeStructureId(record.getFeeStructureId());
            return toRecordResponse(record, structure, components);
        }).toList();
    }

    @Override
    public FeeRecordResponse getReceipt(UUID recordId) {
        StudentFeeRecord record = studentFeeRecordRepository.findById(recordId)
                .orElseThrow(() -> new NotFoundException("Record not found"));
        if (record.getStatus() != FeePaymentStatus.PAID) {
            throw new IllegalStateException("Receipt available only for paid records");
        }
        FeeStructure structure = feeStructureRepository.findById(record.getFeeStructureId()).orElse(null);
        List<FeeComponent> components = feeComponentRepository.findByFeeStructureId(record.getFeeStructureId());
        return toRecordResponse(record, structure, components);
    }

    @Override
    public List<DegreeDepartmentMapping> getDegreeMappings(UUID degreeId) {
        return degreeId == null
                ? degreeDepartmentMappingRepository.findAll()
                : degreeDepartmentMappingRepository.findByDegreeId(degreeId);
    }

    @Override
    @Transactional
    public DegreeDepartmentMapping createDegreeMapping(UUID degreeId, UUID departmentId) {
        if (degreeId == null || departmentId == null) {
            throw new IllegalArgumentException("degreeId and departmentId are required");
        }
        if (degreeDepartmentMappingRepository.existsByDegreeIdAndDepartmentId(degreeId, departmentId)) {
            throw new IllegalStateException("Mapping already exists");
        }
        DegreeDepartmentMapping mapping = new DegreeDepartmentMapping();
        mapping.setDegreeId(degreeId);
        mapping.setDepartmentId(departmentId);
        return degreeDepartmentMappingRepository.save(mapping);
    }

    @Override
    @Transactional
    public void deleteDegreeMapping(UUID degreeId, UUID departmentId) {
        degreeDepartmentMappingRepository.deleteByDegreeIdAndDepartmentId(degreeId, departmentId);
    }

    private void validateStructurePayload(FeeStructureUpsertRequest payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload is required");
        }
        if (payload.title() == null || payload.title().isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (payload.degreeId() == null || payload.departmentId() == null || payload.semester() == null) {
            throw new IllegalArgumentException("degreeId, departmentId and semester are required");
        }
    }

    private List<FeeComponent> saveComponents(UUID structureId, List<FeeComponentRequest> components) {
        List<FeeComponentRequest> requestComponents = components == null ? List.of() : components;
        List<FeeComponent> saved = new ArrayList<>();
        for (FeeComponentRequest item : requestComponents) {
            FeeComponent component = new FeeComponent();
            component.setFeeStructureId(structureId);
            component.setComponentName(item.componentName());
            component.setAmount(item.amount());
            component.setMandatory(item.isMandatory() != null && item.isMandatory());
            saved.add(feeComponentRepository.save(component));
        }
        return saved;
    }

    private FeeStructureResponse toStructureResponse(FeeStructure structure, List<FeeComponent> components) {
        return new FeeStructureResponse(
                structure.getId(),
                structure.getTitle(),
                structure.getDescription(),
                structure.getDegreeId(),
                structure.getDepartmentId(),
                structure.getClassId(),
                structure.getSemester(),
                structure.getDueDate(),
                structure.isActive(),
                structure.getCreatedBy(),
                structure.getCreatedAt(),
                components.stream().map(this::toComponentResponse).toList()
        );
    }

    private FeeComponentResponse toComponentResponse(FeeComponent component) {
        return new FeeComponentResponse(
                component.getId(),
                component.getFeeStructureId(),
                component.getComponentName(),
                component.getAmount(),
                component.isMandatory()
        );
    }

    private FeeRecordResponse toRecordResponse(StudentFeeRecord record, FeeStructure structure, List<FeeComponent> allComponents) {
        Set<String> selectedIds = new HashSet<>();
        if (record.getSelectedComponentIds() != null && !record.getSelectedComponentIds().isBlank()) {
            selectedIds.addAll(Arrays.asList(record.getSelectedComponentIds().split(",")));
        }
        List<FeeComponentResponse> selectedComponents = allComponents.stream()
                .filter(c -> selectedIds.contains(c.getId().toString()))
                .map(this::toComponentResponse)
                .toList();

        return new FeeRecordResponse(
                record.getId(),
                record.getFeeStructureId(),
                record.getStudentId(),
                record.getTotalAmount(),
                record.getStatus(),
                record.getPaymentMode(),
                record.getReceiptNumber(),
                record.getPaidAt(),
                record.getOfflineMarkedBy(),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                selectedComponents,
                structure == null ? null : toStructureResponse(structure, allComponents)
        );
    }
}








