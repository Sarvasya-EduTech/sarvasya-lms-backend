package com.sarvasya.sarvasya_lms_backend.controller;

import com.sarvasya.sarvasya_lms_backend.dto.AuthRequest;
import com.sarvasya.sarvasya_lms_backend.dto.AuthResponse;
import com.sarvasya.sarvasya_lms_backend.dto.SignupRequest;
import com.sarvasya.sarvasya_lms_backend.model.Role;
import com.sarvasya.sarvasya_lms_backend.model.User;
import com.sarvasya.sarvasya_lms_backend.repository.UserRepository;
import com.sarvasya.sarvasya_lms_backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Role userRole = Role.USER;
        if (signupRequest.getRole() != null && !signupRequest.getRole().isBlank()) {
            try {
                userRole = Role.fromValue(signupRequest.getRole());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Error: Invalid role specified. Allowed roles are: sarvasya-admin, admin, professor, user");
            }
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(userRole)
                .build();

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String jwt = jwtUtil.generateToken(userDetails);
        
        User user = userRepository.findByEmail(authRequest.getEmail()).orElseThrow();

        return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(), user.getRole().getValue()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Since JWT is stateless, logout is typically handled on the client side by deleting the token.
        // For a more complete server-side logout, one could implement a token blacklist.
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully.");
    }
}
