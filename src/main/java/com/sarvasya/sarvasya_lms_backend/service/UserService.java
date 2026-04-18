package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.UserCreateRequest;
import com.sarvasya.sarvasya_lms_backend.model.Role;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void bulkCreateUsers(List<UserCreateRequest> requests, Role creatorRole) {
        List<User> usersToSave = new ArrayList<>();

        for (UserCreateRequest req : requests) {
            Role targetRole = Role.fromValue(req.getRole());

            // Validate permission
            if (creatorRole == Role.ADMIN) {
                if (targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                    throw new SecurityException("ADMIN can only create PROFESSOR or USER");
                }
            } else if (creatorRole != Role.SARVASYA_ADMIN) {
                throw new SecurityException("Insufficient permissions for bulk upload");
            }

            if (userRepository.existsByEmail(req.getEmail())) {
                continue; // Skip existing, or handle as update (create/update logic)
            }

            User user = User.builder()
                    .name(req.getName())
                    .email(req.getEmail())
                    .password(passwordEncoder.encode(req.getEmail())) // password = email
                    .role(targetRole)
                    .requiresPasswordChange(true)
                    .isVerified(true) // Bulk created users are likely pre-verified by admin
                    .isActive(true)
                    .build();

            usersToSave.add(user);
        }

        userRepository.saveAll(usersToSave);
    }

    public void createUser(UserCreateRequest req, Role creatorRole) {
        Role targetRole = Role.fromValue(req.getRole());

        if (creatorRole == Role.PROFESSOR && targetRole != Role.USER) {
            throw new SecurityException("PROFESSOR can only create USER");
        }
        
        if (creatorRole == Role.ADMIN) {
            if (targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                throw new SecurityException("ADMIN can only create PROFESSOR or USER");
            }
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getEmail())) // password = email
                .role(targetRole)
                .requiresPasswordChange(true)
                .isVerified(true)
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    public void bulkDeleteUsers(List<UUID> ids) {
        userRepository.deleteAllById(ids);
    }

    public String getBulkUploadTemplate() {
        return "name,email,role\nJohn Doe,john@example.com,user\nJane Smith,jane@example.com,professor";
    }
}
