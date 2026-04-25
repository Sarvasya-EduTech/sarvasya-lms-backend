package com.sarvasya.sarvasya_lms_backend.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.sarvasya.sarvasya_lms_backend.dto.auth.AuthRequest;
import com.sarvasya.sarvasya_lms_backend.dto.auth.AuthResponse;
import com.sarvasya.sarvasya_lms_backend.dto.auth.ChangePasswordRequest;
import com.sarvasya.sarvasya_lms_backend.dto.auth.SignupRequest;
import com.sarvasya.sarvasya_lms_backend.dto.common.MessageResponse;
import com.sarvasya.sarvasya_lms_backend.service.auth.AuthService;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sarvasya/auth/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @PostMapping({"/sarvasya/auth/login", "/{tenantName}/auth/login"})
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping({"/sarvasya/auth/change-password", "/{tenantName}/auth/change-password"})
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) throw new IllegalArgumentException("Unauthorized");
        return ResponseEntity.ok(authService.changePassword(authentication.getName(), request));
    }

    @PostMapping({"/sarvasya/auth/logout", "/{tenantName}/auth/logout"})
    public ResponseEntity<MessageResponse> logoutUser() {
        return ResponseEntity.ok(authService.logout());
    }
}








