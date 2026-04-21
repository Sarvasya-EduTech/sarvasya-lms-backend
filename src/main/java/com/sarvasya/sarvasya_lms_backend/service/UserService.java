package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.UserCreateRequest;
import com.sarvasya.sarvasya_lms_backend.model.*;
import com.sarvasya.sarvasya_lms_backend.repository.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.repository.TenantConfigRepository;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GlobalUserRepository globalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final TenantLimitsService tenantLimitsService;
    private final TenantConfigRepository tenantConfigRepository;

    private void checkRoleLimit(Role role, int additionalCount) {
        long currentCount = userRepository.countByRole(role);
        long newCount = currentCount + additionalCount;

        TenantLimits limits = tenantLimitsService.getLimits();

        if (role == Role.USER && newCount > limits.getUserLimit()) {
            throw new IllegalStateException("User limit exceeded.");
        } else if (role == Role.PROFESSOR && newCount > limits.getProfessorLimit()) {
            throw new IllegalStateException("Professor limit exceeded.");
        } else if (role == Role.ADMIN && newCount > limits.getAdminLimit()) {
            throw new IllegalStateException("Admin limit exceeded.");
        }
    }

    public void bulkCreateUsers(List<UserCreateRequest> requests, Role creatorRole) {
        for (UserCreateRequest req : requests) {
            createUser(req, creatorRole);
        }
    }

    public void createUser(UserCreateRequest req, Role creatorRole) {
        Role targetRole = Role.fromValue(req.getRole());

        // Basic permission check
        validatePermissions(creatorRole, targetRole);

        if (targetRole == Role.TENANT_MANAGER) {
            createGlobalUser(req);
        } else {
            createTenantUser(req);
        }

        // If a TENANT_MANAGER creates a SARVASYA_ADMIN, we should also create a tenant config
        if (creatorRole == Role.TENANT_MANAGER && targetRole == Role.SARVASYA_ADMIN) {
            String tenantId = (req.getTenantId() != null && !req.getTenantId().isBlank())
                    ? req.getTenantId()
                    : extractTenantIdFromEmail(req.getEmail());
            createDefaultTenantConfig(tenantId);
        }

        sendWelcomeEmail(req.getEmail(), req.getName());
    }

    private void validatePermissions(Role creatorRole, Role targetRole) {
        if (creatorRole == Role.USER) {
            throw new SecurityException("USER cannot create any roles");
        } else if (creatorRole == Role.PROFESSOR && targetRole != Role.USER) {
            throw new SecurityException("PROFESSOR can only create USER");
        } else if (creatorRole == Role.ADMIN && targetRole != Role.PROFESSOR && targetRole != Role.USER) {
            throw new SecurityException("ADMIN can only create PROFESSOR or USER");
        } else if (creatorRole == Role.SARVASYA_ADMIN && targetRole == Role.SARVASYA_ADMIN) {
             // can create others
        } else if (creatorRole == Role.TENANT_MANAGER && targetRole == Role.TENANT_MANAGER) {
            throw new SecurityException("TENANT_MANAGER cannot create another TENANT_MANAGER");
        }
    }

    private void createGlobalUser(UserCreateRequest req) {
        if (globalUserRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Global user with this email already exists");
        }

        GlobalUser user = GlobalUser.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(generateDefaultPassword(req.getName())))
                .role(Role.TENANT_MANAGER)
                .requiresPasswordChange(false)
                .isVerified(true)
                .isActive(true)
                .build();

        globalUserRepository.save(user);
    }

    private void createTenantUser(UserCreateRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists in this tenant");
        }

        checkRoleLimit(Role.fromValue(req.getRole()), 1);

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(generateDefaultPassword(req.getName())))
                .role(Role.fromValue(req.getRole()))
                .requiresPasswordChange(true)
                .isVerified(true)
                .isActive(true)
                .classId(parseUuid(req.getClassId()))
                .departmentId(parseUuid(req.getDepartmentId()))
                .degreeId(parseUuid(req.getDegreeId()))
                .build();

        userRepository.save(user);
    }

    private UUID parseUuid(String id) {
        return (id != null && !id.isBlank()) ? UUID.fromString(id) : null;
    }

    private String extractTenantIdFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[1].split("\\.")[0];
        }
        return "default-tenant";
    }

    private void createDefaultTenantConfig(String tenantId) {
        if (tenantConfigRepository.existsById(tenantId)) return;

        Map<String, Object> features = Map.of(
                "basicLms", Map.of(
                        "enabled", true,
                        "modules", Map.of(
                                "attendance", true,
                                "scheduling", true,
                                "bus", true,
                                "academics", true,
                                "fees", true,
                                "notification", true)));

        Map<String, Object> limits = Map.of(
                "users", 1000,
                "professors", 150,
                "admins", 10);

        Map<String, Object> license = Map.of(
                "type", "ENTERPRISE",
                "expiryDate", "2025-12-31");

        TenantConfig config = TenantConfig.builder()
                .tenantId(tenantId)
                .features(features)
                .limits(limits)
                .license(license)
                .build();

        tenantConfigRepository.save(config);
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

    public String getBulkUploadTemplate(Role role) {
        StringBuilder sb = new StringBuilder("name,email,role\n");
        if (role == Role.TENANT_MANAGER) {
            sb.append("Ayesha Khan,ayesha@example.com,sarvasya-admin\n");
            sb.append("Aditi Gupta,aditi@example.com,admin\n");
            sb.append("Sandeep Jain,sandeep@example.com,professor\n");
            sb.append("Aniket Singh,aniket@example.com,user\n");
        } else if (role == Role.SARVASYA_ADMIN) {
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

    public void bulkDeleteUsers(List<UUID> ids, Role creatorRole) {

        List<User> usersToDelete = userRepository.findAllById(ids);
        userRepository.deleteAll(usersToDelete);
    }

    private void sendWelcomeEmail(String email, String name) {
        try {
            String password = generateDefaultPassword(name);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sarvasya.edu.tech@gmail.com");
            message.setTo(email);
            message.setSubject("Welcome to Sarvasya LMS");
            message.setText("Hello " + name + ",\n\nYour account has been created. Password: " + password);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private String generateDefaultPassword(String name) {
        if (name == null || name.trim().isEmpty()) return "password";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[parts.length - 1] + "." + parts[0]).toLowerCase();
        }
        return name.trim().toLowerCase();
    }

    public List<User> getUsersByClassId(UUID classId) {
        return userRepository.findByClassId(classId);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }
}
