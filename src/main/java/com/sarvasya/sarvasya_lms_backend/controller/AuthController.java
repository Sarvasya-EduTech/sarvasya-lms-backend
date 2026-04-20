package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.AuthRequest;
import com.sarvasya.sarvasya_lms_backend.dto.AuthResponse;
import com.sarvasya.sarvasya_lms_backend.dto.ChangePasswordRequest;
import com.sarvasya.sarvasya_lms_backend.dto.SignupRequest;
import com.sarvasya.sarvasya_lms_backend.model.Role;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @org.springframework.beans.factory.annotation.Value("${signup.key}")
    private String signupKey;

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Role userRole = Role.USER;
        if (signupRequest.getRole() != null && !signupRequest.getRole().isBlank()) {
            try {
                userRole = Role.fromValue(signupRequest.getRole());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Error: Invalid role specified.");
            }
        }

        // ONLY TENANT_MANAGER can sign up publicly via the central API
        if (userRole != Role.TENANT_MANAGER) {
            return ResponseEntity.status(403).body("Error: Only tenant managers can sign up here. Other roles must be created via the tenant-specific administration API.");
        }

        // Secure TENANT_MANAGER signup
        if (signupRequest.getSignupKey() == null || !signupRequest.getSignupKey().equals(signupKey)) {
            return ResponseEntity.status(403).body("Error: Invalid or missing signup key.");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(userRole)
                .isVerified(true)
                .isActive(true)
                .requiresPasswordChange(false)
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("Tenant Manager registered successfully.");
    }

    @PostMapping({"/auth/login", "/{tenantName}/auth/login"})
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
 
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();

        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("tenantId", com.sarvasya.sarvasya_lms_backend.security.TenantContext.getTenantId());
        extraClaims.put("id", user.getId().toString());
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRole().getValue());
        if (user.getClassId() != null) {
            extraClaims.put("classId", user.getClassId().toString());
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
            return ResponseEntity.status(401).body("Error: Unauthorized");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Error: User not found");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Error: Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setRequiresPasswordChange(false);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping({"/auth/logout", "/{tenantName}/auth/logout"})
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully.");
    }
}
