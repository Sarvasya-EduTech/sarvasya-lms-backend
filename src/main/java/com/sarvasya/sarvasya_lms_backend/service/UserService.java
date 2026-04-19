package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.UserCreateRequest;
import com.sarvasya.sarvasya_lms_backend.model.Role;
import com.sarvasya.sarvasya_lms_backend.model.TenantLimits;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final TenantLimitsService tenantLimitsService;

    private void checkRoleLimit(Role role, int additionalCount) {
        long currentCount = userRepository.countByRole(role);
        long newCount = currentCount + additionalCount;
        
        TenantLimits limits = tenantLimitsService.getLimits();

        if (role == Role.USER && newCount > limits.getUserLimit()) {
            throw new IllegalStateException("Role Creation Quota limit exceeded. Please purchase more quota limit for it.");
        } else if (role == Role.PROFESSOR && newCount > limits.getProfessorLimit()) {
            throw new IllegalStateException("Role Creation Quota limit exceeded. Please purchase more quota limit for it.");
        } else if (role == Role.ADMIN && newCount > limits.getAdminLimit()) {
            throw new IllegalStateException("Role Creation Quota limit exceeded. Please purchase more quota limit for it.");
        }
    }

    public void bulkCreateUsers(List<UserCreateRequest> requests, Role creatorRole) {
        List<User> usersToSave = new ArrayList<>();

        for (UserCreateRequest req : requests) {
            Role targetRole = Role.fromValue(req.getRole());

            // Validate permission
            if (creatorRole == Role.PROFESSOR) {
                if (targetRole != Role.USER) {
                    throw new SecurityException("PROFESSOR can only create USER");
                }
            } else if (creatorRole == Role.ADMIN) {
                if (targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                    throw new SecurityException("ADMIN can only create PROFESSOR or USER");
                }
            } else if (creatorRole != Role.SARVASYA_ADMIN) {
                throw new SecurityException("Insufficient permissions for bulk upload");
            }

            if (userRepository.existsByEmail(req.getEmail())) {
                continue; // Skip existing, or handle as update (create/update logic)
            }
            // Check limits including currently pending saves
            long pendingCount = usersToSave.stream().filter(u -> u.getRole() == targetRole).count();
            checkRoleLimit(targetRole, (int) pendingCount + 1);

            String password = generateDefaultPassword(req.getName());
            User user = User.builder()
                    .name(req.getName())
                    .email(req.getEmail())
                    .password(passwordEncoder.encode(password)) // password = lastname.firstname
                    .role(targetRole)
                    .requiresPasswordChange(true)
                    .isVerified(true) // Bulk created users are likely pre-verified by admin
                    .isActive(true)
                    .build();

            usersToSave.add(user);
            sendWelcomeEmail(user.getEmail(), req.getName());
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

        checkRoleLimit(targetRole, 1);

        String password = generateDefaultPassword(req.getName());
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(password)) // password = lastname.firstname
                .role(targetRole)
                .requiresPasswordChange(true)
                .isVerified(true)
                .isActive(true)
                .build();

        userRepository.save(user);
        sendWelcomeEmail(user.getEmail(), req.getName());
    }

    public void bulkDeleteUsers(List<UUID> ids) {
        userRepository.deleteAllById(ids);
    }

    public String getBulkUploadTemplate(Role role) {
        StringBuilder sb = new StringBuilder("name,email,role\n");
        if (role == Role.SARVASYA_ADMIN) {
            sb.append("Aditi Gupta,aditi@example.com,admin\n");
            sb.append("Sandeep Jain,sandeep@example.com,professor\n");
            sb.append("Aniket Singh,aniket@example.com,user\n");
        } else if (role == Role.ADMIN) {
            sb.append("Sandeep Jain,sandeep@example.com,professor\n");
            sb.append("Aniket Singh,aniket@example.com,user\n");
        } else if (role == Role.PROFESSOR) {
            sb.append("Aniket Singh,aniket@example.com,user\n");
        }
        return sb.toString();
    }

    public void processBulkCsv(String csvContent, Role creatorRole) {
        String[] lines = csvContent.split("\n");
        List<UserCreateRequest> requests = new ArrayList<>();

        // Skip header if present
        int startIndex = (lines.length > 0 && lines[0].toLowerCase().contains("email")) ? 1 : 0;

        for (int i = startIndex; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty())
                continue;

            String[] columns = line.split(",");
            if (columns.length < 3)
                continue;

            UserCreateRequest req = new UserCreateRequest();
            req.setName(columns[0].trim());
            req.setEmail(columns[1].trim());
            req.setRole(columns[2].trim());
            requests.add(req);
        }

        bulkCreateUsers(requests, creatorRole);
    }

    private void sendWelcomeEmail(String email, String name) {
        try {
            String password = generateDefaultPassword(name);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sarvasya.edu.tech@gmail.com");
            message.setTo(email);
            message.setSubject("Welcome to Sarvasya LMS");
            message.setText("Hello " + name + ",\n\n" +
                    "Your account has been created on Sarvasya LMS.\n" +
                    "You can login using your email: " + email + "\n" +
                    "Your default password is: " + password + "\n\n" +
                    "Note: You will be required to change your password upon your first login for security reasons.\n\n" +
                    "Best regards,\nSarvasya Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to " + email + ": " + e.getMessage());
        }
    }

    private String generateDefaultPassword(String name) {
        if (name == null || name.trim().isEmpty())
            return "password";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            // "Pratham Sharma" -> "sharma.pratham"
            return (parts[parts.length - 1] + "." + parts[0]).toLowerCase();
        }
        return name.trim().toLowerCase();
    }
}
