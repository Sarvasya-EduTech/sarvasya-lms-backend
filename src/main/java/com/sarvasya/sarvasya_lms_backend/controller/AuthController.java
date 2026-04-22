package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.AuthRequest;
import com.sarvasya.sarvasya_lms_backend.dto.AuthResponse;
import com.sarvasya.sarvasya_lms_backend.dto.ChangePasswordRequest;
import com.sarvasya.sarvasya_lms_backend.dto.SignupRequest;
import com.sarvasya.sarvasya_lms_backend.model.BaseUser;
import com.sarvasya.sarvasya_lms_backend.model.GlobalUser;
import com.sarvasya.sarvasya_lms_backend.model.Role;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final GlobalUserRepository globalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${signup.key}")
    private String signupKey;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        // Global signup is only for TENANT_MANAGERs in the central 'tenant' schema
        if (globalUserRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is already in use!"));
        }

        Role userRole = Role.USER;
        if (signupRequest.getRole() != null && !signupRequest.getRole().isBlank()) {
            try {
                userRole = Role.fromValue(signupRequest.getRole());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid role specified."));
            }
        }

        // ONLY TENANT_MANAGER can sign up publicly via the central API
        if (userRole != Role.TENANT_MANAGER) {
            return ResponseEntity.status(403).body(Map.of("error", "Only tenant managers can sign up here."));
        }

        // Secure TENANT_MANAGER signup
        if (signupRequest.getSignupKey() == null || !signupRequest.getSignupKey().equals(signupKey)) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid or missing signup key."));
        }

        GlobalUser user = GlobalUser.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(userRole)
                .isVerified(true)
                .isActive(true)
                .requiresPasswordChange(false)
                .build();

        globalUserRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Tenant Manager registered successfully."));
    }

    @PostMapping({"/auth/login", "/{tenantName}/auth/login"})
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
 
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // Try to find the user in the correct schema context
        BaseUser user;
        String currentTenant = TenantContext.getTenantId();

        if ("tenant".equals(currentTenant)) {
            // In global context, use globalUserRepository directly to avoid column mismatch errors
            user = globalUserRepository.findByEmail(authRequest.getEmail()).orElse(null);
        } else {
            // First try current tenant schema
            user = userRepository.findByEmail(authRequest.getEmail()).orElse(null);

            // Fallback: If not found in tenant, check global schema
            if (user == null) {
                user = globalUserRepository.findByEmail(authRequest.getEmail()).orElse(null);
            }
        }
        
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }


        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("tenantId", TenantContext.getTenantId());
        extraClaims.put("id", user.getId().toString());
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRole().getValue());
        
        // Add academic fields only if it's a tenant-specific User
        if (user instanceof User tenantUser) {
            if (tenantUser.getClassId() != null) {
                extraClaims.put("classId", tenantUser.getClassId().toString());
            }
        }

        String jwt = jwtUtil.generateToken(userDetails, extraClaims);
        
        AuthResponse.UserDto userDto = new AuthResponse.UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getValue(),
                user.isRequiresPasswordChange()
        );

        return ResponseEntity.ok(new AuthResponse(jwt, userDto));
    }

    @PostMapping({"/auth/change-password", "/{tenantName}/auth/change-password"})
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String email = authentication.getName();
        String currentTenant = TenantContext.getTenantId();
        
        // Find user - could be in current tenant or global
        BaseUser user;
        if ("tenant".equals(currentTenant)) {
            user = globalUserRepository.findByEmail(email).orElse(null);
        } else {
            user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = globalUserRepository.findByEmail(email).orElse(null);
            }
        }

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }


        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid current password"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setRequiresPasswordChange(false);
        
        // Save back to the correct repository
        if (user instanceof User tenantUser) {
            userRepository.save(tenantUser);
        } else if (user instanceof GlobalUser globalUser) {
            globalUserRepository.save(globalUser);
        }

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PostMapping({"/auth/logout", "/{tenantName}/auth/logout"})
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}
