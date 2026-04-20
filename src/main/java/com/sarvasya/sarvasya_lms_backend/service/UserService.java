package com.sarvasya.sarvasya_lms_backend.service;

import com.sarvasya.sarvasya_lms_backend.dto.UserCreateRequest;
import com.sarvasya.sarvasya_lms_backend.model.Role;
import com.sarvasya.sarvasya_lms_backend.model.TenantLimits;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import com.sarvasya.sarvasya_lms_backend.repository.TenantConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
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
    private final com.sarvasya.sarvasya_lms_backend.repository.TenantConfigRepository tenantConfigRepository;

    private void checkRoleLimit(Role role, int additionalCount) {
        long currentCount = userRepository.countByRole(role);
        long newCount = currentCount + additionalCount;

        TenantLimits limits = tenantLimitsService.getLimits();

        if (role == Role.USER && newCount > limits.getUserLimit()) {
            throw new IllegalStateException(
                    "Role Creation Quota limit exceeded. Please purchase more quota limit for it.");
        } else if (role == Role.PROFESSOR && newCount > limits.getProfessorLimit()) {
            throw new IllegalStateException(
                    "Role Creation Quota limit exceeded. Please purchase more quota limit for it.");
        } else if (role == Role.ADMIN && newCount > limits.getAdminLimit()) {
            throw new IllegalStateException(
                    "Role Creation Quota limit exceeded. Please purchase more quota limit for it.");
        }
    }

    public void bulkCreateUsers(List<UserCreateRequest> requests, Role creatorRole) {
        List<User> usersToSave = new ArrayList<>();

        for (UserCreateRequest req : requests) {
            Role targetRole = Role.fromValue(req.getRole());

            // Validate permission based on creator role
            if (creatorRole == Role.USER) {
                throw new SecurityException("USER cannot create any roles");
            } else if (creatorRole == Role.PROFESSOR) {
                if (targetRole != Role.USER) {
                    throw new SecurityException("PROFESSOR can only create USER");
                }
            } else if (creatorRole == Role.ADMIN) {
                if (targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                    throw new SecurityException("ADMIN can only create PROFESSOR or USER");
                }
            } else if (creatorRole == Role.SARVASYA_ADMIN) {
                if (targetRole != Role.ADMIN && targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                    throw new SecurityException("SARVASYA_ADMIN can only create ADMIN, PROFESSOR, or USER");
                }
            } else if (creatorRole == Role.TENANT_MANAGER) {
                if (targetRole == Role.TENANT_MANAGER) {
                    throw new SecurityException("TENANT_MANAGER cannot create another TENANT_MANAGER");
                }
                // tenant-manager can create SARVASYA_ADMIN, ADMIN, PROFESSOR, USER
            } else {
                throw new SecurityException("Insufficient permissions for bulk upload");
            }

            if (userRepository.existsByEmail(req.getEmail())) {
                continue; // Skip existing, or handle as update (create/update logic)
            }
            String currentTenant = TenantContext.getTenantId();
            if ((currentTenant == null || "tenant".equals(currentTenant)) && targetRole != Role.TENANT_MANAGER) {
                throw new IllegalStateException("Only TENANT_MANAGER can be saved in the central tenant schema.");
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
                    .classId(req.getClassId() != null && !req.getClassId().isEmpty() ? UUID.fromString(req.getClassId()) : null)
                    .departmentId(req.getDepartmentId() != null && !req.getDepartmentId().isEmpty() ? UUID.fromString(req.getDepartmentId()) : null)
                    .degreeId(req.getDegreeId() != null && !req.getDegreeId().isEmpty() ? UUID.fromString(req.getDegreeId()) : null)
                    .build();

            usersToSave.add(user);

            // If a TENANT_MANAGER creates a SARVASYA_ADMIN, we should also create a tenant
            // config
            if (creatorRole == Role.TENANT_MANAGER && targetRole == Role.SARVASYA_ADMIN) {
                String tenantId = (req.getTenantId() != null && !req.getTenantId().isBlank())
                        ? req.getTenantId()
                        : extractTenantIdFromEmail(req.getEmail());
                createDefaultTenantConfig(tenantId);
            }

            sendWelcomeEmail(user.getEmail(), req.getName());
        }

        userRepository.saveAllAndFlush(usersToSave);
    }

    public void createUser(UserCreateRequest req, Role creatorRole) {
        Role targetRole = Role.fromValue(req.getRole());

        if (creatorRole == Role.USER) {
            throw new SecurityException("USER cannot create any roles");
        } else if (creatorRole == Role.PROFESSOR) {
            if (targetRole != Role.USER) {
                throw new SecurityException("PROFESSOR can only create USER");
            }
        } else if (creatorRole == Role.ADMIN) {
            if (targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                throw new SecurityException("ADMIN can only create PROFESSOR or USER");
            }
        } else if (creatorRole == Role.SARVASYA_ADMIN) {
            if (targetRole != Role.ADMIN && targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                throw new SecurityException("SARVASYA_ADMIN can only create ADMIN, PROFESSOR, or USER");
            }
        } else if (creatorRole == Role.TENANT_MANAGER) {
            if (targetRole == Role.TENANT_MANAGER) {
                throw new SecurityException("TENANT_MANAGER cannot create another TENANT_MANAGER");
            }
        } else {
            throw new SecurityException("Insufficient permissions for user creation");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        String currentTenant = TenantContext.getTenantId();
        if ((currentTenant == null || "tenant".equals(currentTenant)) && targetRole != Role.TENANT_MANAGER) {
            throw new IllegalStateException("Only TENANT_MANAGER can be saved in the central tenant schema.");
        }

        checkRoleLimit(targetRole, 1);

        String password = generateDefaultPassword(req.getName());
        boolean passwordChangeRequired = (targetRole != Role.TENANT_MANAGER);

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(password))
                .role(targetRole)
                .requiresPasswordChange(passwordChangeRequired)
                .isVerified(true)
                .isActive(true)
                .classId(req.getClassId() != null && !req.getClassId().isEmpty() ? UUID.fromString(req.getClassId()) : null)
                .departmentId(req.getDepartmentId() != null && !req.getDepartmentId().isEmpty() ? UUID.fromString(req.getDepartmentId()) : null)
                .degreeId(req.getDegreeId() != null && !req.getDegreeId().isEmpty() ? UUID.fromString(req.getDegreeId()) : null)
                .build();

        userRepository.saveAndFlush(user);
        System.out.println("DEBUG: User saved and flushed to schema: " + TenantContext.getTenantId());

        // If a TENANT_MANAGER creates a SARVASYA_ADMIN, we should also create a tenant
        // config
        if (creatorRole == Role.TENANT_MANAGER && targetRole == Role.SARVASYA_ADMIN) {
            String tenantId = (req.getTenantId() != null && !req.getTenantId().isBlank())
                    ? req.getTenantId()
                    : extractTenantIdFromEmail(req.getEmail());
            createDefaultTenantConfig(tenantId);
        }

        sendWelcomeEmail(user.getEmail(), req.getName());
    }

    private String extractTenantIdFromEmail(String email) {
        // Simple logic: extract domain or use a default if needed
        if (email.contains("@")) {
            return email.split("@")[1].split("\\.")[0];
        }
        return "default-tenant";
    }

    private void createDefaultTenantConfig(String tenantId) {
        String originalTenant = TenantContext.getTenantId();
        try {
            TenantContext.setTenantId("tenant");

            if (tenantConfigRepository.existsById(tenantId))
                return;

            java.util.Map<String, Object> features = java.util.Map.of(
                    "basicLms", java.util.Map.of(
                            "enabled", true,
                            "modules", java.util.Map.of(
                                    "attendance", true,
                                    "scheduling", true,
                                    "bus", true,
                                    "academics", true,
                                    "fees", true,
                                    "notification", true)),
                    "basicLicense", java.util.Map.of("enabled", false),
                    "advancedLicense", java.util.Map.of("enabled", false));

            java.util.Map<String, Object> limits = java.util.Map.of(
                    "users", 1000,
                    "professors", 150,
                    "admins", 10);

            java.util.Map<String, Object> license = java.util.Map.of(
                    "type", "ENTERPRISE",
                    "expiryDate", "2025-12-31");

            com.sarvasya.sarvasya_lms_backend.model.TenantConfig config = com.sarvasya.sarvasya_lms_backend.model.TenantConfig
                    .builder()
                    .tenantId(tenantId)
                    .features(features)
                    .limits(limits)
                    .license(license)
                    .build();

            tenantConfigRepository.save(config);
        } finally {
            TenantContext.setTenantId(originalTenant);
        }
    }

    public void bulkDeleteUsers(List<UUID> ids, Role creatorRole) {
        if (creatorRole == Role.USER) {
            throw new SecurityException("USER cannot delete any roles");
        }

        List<User> usersToDelete = userRepository.findAllById(ids);
        for (User targetUser : usersToDelete) {
            Role targetRole = targetUser.getRole();

            if (creatorRole == Role.PROFESSOR && targetRole != Role.USER) {
                throw new SecurityException("PROFESSOR can only delete USER");
            } else if (creatorRole == Role.ADMIN) {
                if (targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                    throw new SecurityException("ADMIN can only delete PROFESSOR or USER");
                }
            } else if (creatorRole == Role.SARVASYA_ADMIN) {
                if (targetRole != Role.ADMIN && targetRole != Role.PROFESSOR && targetRole != Role.USER) {
                    throw new SecurityException("SARVASYA_ADMIN can only delete ADMIN, PROFESSOR, or USER");
                }
            } else if (creatorRole == Role.TENANT_MANAGER) {
                if (targetRole == Role.TENANT_MANAGER) {
                    throw new SecurityException("TENANT_MANAGER cannot delete another TENANT_MANAGER");
                }
            }
        }

        userRepository.deleteAll(usersToDelete);
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
                    "Note: You will be required to change your password upon your first login for security reasons.\n\n"
                    +
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
