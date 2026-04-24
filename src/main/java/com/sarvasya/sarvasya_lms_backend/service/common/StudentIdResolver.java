package com.sarvasya.sarvasya_lms_backend.service.common;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.sarvasya.sarvasya_lms_backend.repository.user.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.repository.user.UserRepository;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;

@Service
@RequiredArgsConstructor
public class StudentIdResolver {
    private final UserRepository userRepository;
    private final GlobalUserRepository globalUserRepository;

    public UUID resolveStudentId(String studentIdOrEmail) {
        if (studentIdOrEmail == null || studentIdOrEmail.isBlank()) {
            return resolveCurrentUserId()
                    .orElseThrow(() -> new IllegalArgumentException("studentId is required"));
        }
        try {
            return UUID.fromString(studentIdOrEmail);
        } catch (IllegalArgumentException ignored) {
            return resolveUserIdByEmail(studentIdOrEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown student email: " + studentIdOrEmail));
        }
    }

    public Optional<UUID> resolveCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) return Optional.empty();
        return resolveUserIdByEmail(auth.getName());
    }

    private Optional<UUID> resolveUserIdByEmail(String email) {
        String currentTenant = TenantContext.getTenantId();
        if ("tenant".equals(currentTenant)) {
            return globalUserRepository.findByEmail(email).map(u -> u.getId());
        }
        // Prefer current tenant schema, then fallback to global.
        return userRepository.findByEmail(email)
                .map(u -> u.getId())
                .or(() -> globalUserRepository.findByEmail(email).map(u -> u.getId()));
    }
}









