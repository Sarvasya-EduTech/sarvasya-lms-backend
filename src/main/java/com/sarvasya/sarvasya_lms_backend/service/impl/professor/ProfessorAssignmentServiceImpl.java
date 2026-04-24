package com.sarvasya.sarvasya_lms_backend.service.impl.professor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.dto.professor.ProfessorAssignmentRequest;
import com.sarvasya.sarvasya_lms_backend.model.professor.ProfessorCourseClassAssignment;
import com.sarvasya.sarvasya_lms_backend.repository.professor.ProfessorCourseClassAssignmentRepository;
import com.sarvasya.sarvasya_lms_backend.service.professor.ProfessorAssignmentService;

@Service
@RequiredArgsConstructor
public class ProfessorAssignmentServiceImpl implements ProfessorAssignmentService {
    private final ProfessorCourseClassAssignmentRepository repository;

    @Override
    public List<ProfessorCourseClassAssignment> getAll() {
        return repository.findAll();
    }

    @Override
    public List<ProfessorCourseClassAssignment> getByProfessorId(UUID professorId) {
        return repository.findByProfessorId(professorId);
    }

    @Override
    public boolean isProfessorAssigned(UUID professorId, UUID classId, UUID courseId) {
        return repository.existsByProfessorIdAndClassIdAndCourseId(professorId, classId, courseId);
    }

    @Override
    @Transactional
    public List<ProfessorCourseClassAssignment> createBulk(ProfessorAssignmentRequest request) {
        if (request.getProfessorId() == null) {
            throw new IllegalArgumentException("professorId is required");
        }
        if (request.getClassIds() == null || request.getClassIds().isEmpty()) {
            throw new IllegalArgumentException("classIds are required");
        }
        if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {
            throw new IllegalArgumentException("courseIds are required");
        }

        List<ProfessorCourseClassAssignment> created = new ArrayList<>();
        for (UUID classId : request.getClassIds()) {
            for (UUID courseId : request.getCourseIds()) {
                if (!repository.existsByProfessorIdAndClassIdAndCourseId(request.getProfessorId(), classId, courseId)) {
                    ProfessorCourseClassAssignment item = new ProfessorCourseClassAssignment();
                    item.setProfessorId(request.getProfessorId());
                    item.setClassId(classId);
                    item.setCourseId(courseId);
                    created.add(repository.save(item));
                }
            }
        }
        return created;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void bulkDelete(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return;
        repository.deleteAllById(ids);
    }
}








