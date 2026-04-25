package com.sarvasya.sarvasya_lms_backend.service.impl.auth;

import com.sarvasya.sarvasya_lms_backend.model.user.BaseUser;
import com.sarvasya.sarvasya_lms_backend.model.user.GlobalUser;
import com.sarvasya.sarvasya_lms_backend.model.user.User;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sarvasya.sarvasya_lms_backend.dto.auth.AuthRequest;
import com.sarvasya.sarvasya_lms_backend.dto.auth.AuthResponse;
import com.sarvasya.sarvasya_lms_backend.dto.auth.ChangePasswordRequest;
import com.sarvasya.sarvasya_lms_backend.dto.auth.SignupRequest;
import com.sarvasya.sarvasya_lms_backend.dto.common.MessageResponse;
import com.sarvasya.sarvasya_lms_backend.model.common.Role;
import com.sarvasya.sarvasya_lms_backend.repository.user.GlobalUserRepository;
import com.sarvasya.sarvasya_lms_backend.repository.user.UserRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import com.sarvasya.sarvasya_lms_backend.security.TenantContext;
import com.sarvasya.sarvasya_lms_backend.service.auth.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final GlobalUserRepository globalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${signup.key}")
    private String signupKey;

    @Override
    @Transactional
    public MessageResponse signup(SignupRequest signupRequest) {
        if (globalUserRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }

        Role userRole = Role.USER;
        if (signupRequest.getRole() != null && !signupRequest.getRole().isBlank()) {
            userRole = Role.fromValue(signupRequest.getRole());
        }

        if (userRole != Role.TENANT_MANAGER) {
            throw new SecurityException("Only tenant managers can sign up here");
        }

        if (signupRequest.getSignupKey() == null || !signupRequest.getSignupKey().equals(signupKey)) {
            throw new SecurityException("Invalid or missing signup key");
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

        return new MessageResponse("Tenant Manager registered successfully.");
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        BaseUser user = findUserForLogin(authRequest.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("tenantId", TenantContext.getTenantId());
        extraClaims.put("id", user.getId().toString());
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRole().getValue());

        if (user instanceof User tenantUser && tenantUser.getClassId() != null) {
            extraClaims.put("classId", tenantUser.getClassId().toString());
        }

        String jwt = jwtUtil.generateToken(userDetails, extraClaims);

        AuthResponse.UserDto userDto = new AuthResponse.UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getValue(),
                user.isRequiresPasswordChange()
        );

        return new AuthResponse(jwt, userDto);
    }

    private BaseUser findUserForLogin(String email) {
        String currentTenant = TenantContext.getTenantId();
        if ("tenant".equals(currentTenant)) {
            return globalUserRepository.findByEmail(email).orElse(null);
        }
        BaseUser user = userRepository.findByEmail(email).orElse(null);
        return user != null ? user : globalUserRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public MessageResponse changePassword(String email, ChangePasswordRequest request) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Unauthorized");

        String currentTenant = TenantContext.getTenantId();

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
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setRequiresPasswordChange(false);

        if (user instanceof User tenantUser) {
            userRepository.save(tenantUser);
        } else if (user instanceof GlobalUser globalUser) {
            globalUserRepository.save(globalUser);
        }

        return new MessageResponse("Password changed successfully");
    }

    @Override
    public MessageResponse logout() {
        SecurityContextHolder.clearContext();
        return new MessageResponse("Logged out successfully.");
    }
}









