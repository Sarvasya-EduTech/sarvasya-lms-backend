package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.model.*;
import com.sarvasya.sarvasya_lms_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeManagementService {
    private final FeeStructureRepository feeStructureRepository;
    private final FeeComponentRepository feeComponentRepository;
    private final StudentFeeRecordRepository studentFeeRecordRepository;
    private final DegreeDepartmentMappingRepository degreeDepartmentMappingRepository;

    @Transactional
    public Map<String, Object> createStructure(Map<String, Object> payload) {
        UUID degreeId = UUID.fromString(payload.get("degreeId").toString());
        UUID departmentId = UUID.fromString(payload.get("departmentId").toString());
        UUID classId = null;
        if (payload.get("classId") != null && !payload.get("classId").toString().isBlank()) {
            classId = UUID.fromString(payload.get("classId").toString());
        }

        if (!degreeDepartmentMappingRepository.existsByDegreeIdAndDepartmentId(degreeId, departmentId)) {
            throw new RuntimeException("Department is not mapped to selected degree");
        }

        FeeStructure structure = new FeeStructure();
        structure.setTitle(payload.get("title").toString());
        structure.setDescription((String) payload.getOrDefault("description", null));
        structure.setDegreeId(degreeId);
        structure.setDepartmentId(departmentId);
        structure.setClassId(classId);
        structure.setSemester(Integer.parseInt(payload.get("semester").toString()));
        structure.setActive(Boolean.parseBoolean(payload.getOrDefault("isActive", true).toString()));
        if (payload.get("createdBy") != null) {
            structure.setCreatedBy(UUID.fromString(payload.get("createdBy").toString()));
        }
        if (payload.get("dueDate") != null && !payload.get("dueDate").toString().isBlank()) {
            structure.setDueDate(java.time.LocalDate.parse(payload.get("dueDate").toString()));
        }
        FeeStructure savedStructure = feeStructureRepository.save(structure);

        List<Map<String, Object>> components = (List<Map<String, Object>>) payload.getOrDefault("components", Collections.emptyList());
        List<FeeComponent> savedComponents = new ArrayList<>();
        for (Map<String, Object> item : components) {
            FeeComponent component = new FeeComponent();
            component.setFeeStructureId(savedStructure.getId());
            component.setComponentName(item.get("componentName").toString());
            component.setAmount(new BigDecimal(item.get("amount").toString()));
            component.setMandatory(Boolean.parseBoolean(item.getOrDefault("isMandatory", false).toString()));
            savedComponents.add(feeComponentRepository.save(component));
        }

        return toStructureResponse(savedStructure, savedComponents);
    }

    @Transactional
    public Map<String, Object> updateStructure(UUID structureId, Map<String, Object> payload) {
        FeeStructure structure = feeStructureRepository.findById(structureId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        UUID degreeId = UUID.fromString(payload.get("degreeId").toString());
        UUID departmentId = UUID.fromString(payload.get("departmentId").toString());
        UUID classId = null;
        if (payload.get("classId") != null && !payload.get("classId").toString().isBlank()) {
            classId = UUID.fromString(payload.get("classId").toString());
        }
        if (!degreeDepartmentMappingRepository.existsByDegreeIdAndDepartmentId(degreeId, departmentId)) {
            throw new RuntimeException("Department is not mapped to selected degree");
        }

        structure.setTitle(payload.get("title").toString());
        structure.setDescription((String) payload.getOrDefault("description", null));
        structure.setDegreeId(degreeId);
        structure.setDepartmentId(departmentId);
        structure.setClassId(classId);
        structure.setSemester(Integer.parseInt(payload.get("semester").toString()));
        structure.setActive(Boolean.parseBoolean(payload.getOrDefault("isActive", true).toString()));
        if (payload.get("dueDate") != null && !payload.get("dueDate").toString().isBlank()) {
            structure.setDueDate(java.time.LocalDate.parse(payload.get("dueDate").toString()));
        } else {
            structure.setDueDate(null);
        }
        FeeStructure savedStructure = feeStructureRepository.save(structure);

        feeComponentRepository.deleteByFeeStructureId(savedStructure.getId());
        List<Map<String, Object>> components = (List<Map<String, Object>>) payload.getOrDefault("components", Collections.emptyList());
        List<FeeComponent> savedComponents = new ArrayList<>();
        for (Map<String, Object> item : components) {
            FeeComponent component = new FeeComponent();
            component.setFeeStructureId(savedStructure.getId());
            component.setComponentName(item.get("componentName").toString());
            component.setAmount(new BigDecimal(item.get("amount").toString()));
            component.setMandatory(Boolean.parseBoolean(item.getOrDefault("isMandatory", false).toString()));
            savedComponents.add(feeComponentRepository.save(component));
        }
        return toStructureResponse(savedStructure, savedComponents);
    }

    public List<Map<String, Object>> getStructures(UUID degreeId, UUID departmentId, UUID classId, Integer semester, Boolean activeOnly) {
        List<FeeStructure> structures;
        if (classId != null) {
            if (activeOnly != null) {
                structures = feeStructureRepository.findByClassIdAndIsActive(classId, activeOnly);
            } else {
                structures = feeStructureRepository.findByClassId(classId);
            }
            if (semester != null) {
                final Integer sem = semester;
                structures = structures.stream()
                        .filter(s -> Objects.equals(s.getSemester(), sem))
                        .collect(Collectors.toList());
            }
        } else if (degreeId != null && departmentId != null && semester != null) {
            structures = feeStructureRepository.findByDegreeIdAndDepartmentIdAndSemesterAndIsActive(degreeId, departmentId, semester, activeOnly == null || activeOnly);
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
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> studentOptAndCreateRecord(UUID feeStructureId, Map<String, Object> payload) {
        FeeStructure structure = feeStructureRepository.findById(feeStructureId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        UUID studentId = UUID.fromString(payload.get("studentId").toString());
        List<FeeComponent> components = feeComponentRepository.findByFeeStructureId(feeStructureId);
        Set<UUID> chosenOptionalIds = ((List<Object>) payload.getOrDefault("optionalComponentIds", Collections.emptyList()))
                .stream()
                .map(String::valueOf)
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        List<FeeComponent> selected = components.stream()
                .filter(c -> c.isMandatory() || chosenOptionalIds.contains(c.getId()))
                .toList();

        BigDecimal total = selected.stream()
                .map(FeeComponent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StudentFeeRecord record = studentFeeRecordRepository.findByFeeStructureIdAndStudentId(feeStructureId, studentId)
                .orElseGet(StudentFeeRecord::new);
        record.setFeeStructureId(feeStructureId);
        record.setStudentId(studentId);
        record.setSelectedComponentIds(selected.stream().map(c -> c.getId().toString()).collect(Collectors.joining(",")));
        record.setTotalAmount(total);
        if (record.getStatus() == null) {
            record.setStatus(FeePaymentStatus.UNPAID);
        }
        StudentFeeRecord saved = studentFeeRecordRepository.save(record);
        return toRecordResponse(saved, structure, components);
    }

    @Transactional
    public Map<String, Object> markPaid(UUID recordId, FeePaymentMode mode, UUID adminUserId) {
        StudentFeeRecord record = studentFeeRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));
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

    public List<Map<String, Object>> getRecords(UUID studentId, FeePaymentStatus status) {
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
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getReceipt(UUID recordId) {
        StudentFeeRecord record = studentFeeRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        if (record.getStatus() != FeePaymentStatus.PAID) {
            throw new RuntimeException("Receipt available only for paid records");
        }
        FeeStructure structure = feeStructureRepository.findById(record.getFeeStructureId()).orElse(null);
        List<FeeComponent> components = feeComponentRepository.findByFeeStructureId(record.getFeeStructureId());
        return toRecordResponse(record, structure, components);
    }

    public List<DegreeDepartmentMapping> getDegreeMappings(UUID degreeId) {
        if (degreeId == null) {
            return degreeDepartmentMappingRepository.findAll();
        }
        return degreeDepartmentMappingRepository.findByDegreeId(degreeId);
    }

    @Transactional
    public DegreeDepartmentMapping createDegreeMapping(UUID degreeId, UUID departmentId) {
        if (degreeDepartmentMappingRepository.existsByDegreeIdAndDepartmentId(degreeId, departmentId)) {
            throw new RuntimeException("Mapping already exists");
        }
        DegreeDepartmentMapping mapping = new DegreeDepartmentMapping();
        mapping.setDegreeId(degreeId);
        mapping.setDepartmentId(departmentId);
        return degreeDepartmentMappingRepository.save(mapping);
    }

    @Transactional
    public void deleteDegreeMapping(UUID degreeId, UUID departmentId) {
        degreeDepartmentMappingRepository.deleteByDegreeIdAndDepartmentId(degreeId, departmentId);
    }

    private Map<String, Object> toStructureResponse(FeeStructure structure, List<FeeComponent> components) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", structure.getId());
        out.put("title", structure.getTitle());
        out.put("description", structure.getDescription());
        out.put("degreeId", structure.getDegreeId());
        out.put("departmentId", structure.getDepartmentId());
        out.put("classId", structure.getClassId());
        out.put("semester", structure.getSemester());
        out.put("dueDate", structure.getDueDate());
        out.put("isActive", structure.isActive());
        out.put("createdBy", structure.getCreatedBy());
        out.put("createdAt", structure.getCreatedAt());
        out.put("components", components);
        return out;
    }

    private Map<String, Object> toRecordResponse(StudentFeeRecord record, FeeStructure structure, List<FeeComponent> allComponents) {
        Set<String> selectedIds = new HashSet<>();
        if (record.getSelectedComponentIds() != null && !record.getSelectedComponentIds().isBlank()) {
            selectedIds.addAll(Arrays.asList(record.getSelectedComponentIds().split(",")));
        }
        List<FeeComponent> selectedComponents = allComponents.stream()
                .filter(c -> selectedIds.contains(c.getId().toString()))
                .toList();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", record.getId());
        out.put("feeStructureId", record.getFeeStructureId());
        out.put("studentId", record.getStudentId());
        out.put("totalAmount", record.getTotalAmount());
        out.put("status", record.getStatus());
        out.put("paymentMode", record.getPaymentMode());
        out.put("receiptNumber", record.getReceiptNumber());
        out.put("paidAt", record.getPaidAt());
        out.put("offlineMarkedBy", record.getOfflineMarkedBy());
        out.put("createdAt", record.getCreatedAt());
        out.put("updatedAt", record.getUpdatedAt());
        out.put("selectedComponents", selectedComponents);
        if (structure != null) {
            out.put("feeStructure", toStructureResponse(structure, allComponents));
        }
        return out;
    }
}
