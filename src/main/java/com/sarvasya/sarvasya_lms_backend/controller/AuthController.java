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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenantName}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@PathVariable String tenantName, @Valid @RequestBody SignupRequest signupRequest) {
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
                .isVerified(false)
                .isActive(false)
                .requiresPasswordChange(signupRequest.isRequiresPasswordChange())
                .build();

        userRepository.save(user);

        if (userRole == Role.SARVASYA_ADMIN) {
            String token = jwtUtil.generateTokenForEmail(user.getEmail());
            String verificationLink = "http://localhost:8080/api/" + tenantName + "/auth/verify-email?token=" + token;
            
            sendVerificationEmail(user.getEmail(), verificationLink);
            
            System.out.println("\n=====================================================");
            System.out.println("VERIFICATION EMAIL SENT TO: sarvasya.edu.tech@gmail.com");
            System.out.println("Verification Link: " + verificationLink);
            System.out.println("=====================================================\n");
        }

        return ResponseEntity.ok("User registered successfully. Verification is pending.");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@PathVariable String tenantName, @RequestParam String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email).orElse(null);
            
            if (user == null) {
                return ResponseEntity.badRequest().body("Error: User not found for this token.");
            }
            
            user.setIsVerified(true);
            user.setIsActive(true);
            userRepository.save(user);
            
            return ResponseEntity.ok("Account verified and activated successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: Invalid or expired verification token.");
        }
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

        AuthResponse.UserDto userDto = new AuthResponse.UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getValue(),
                user.isRequiresPasswordChange()
        );

        return ResponseEntity.ok(new AuthResponse(jwt, userDto));
    }

    @PostMapping("/change-password")
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

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Since JWT is stateless, logout is typically handled on the client side by deleting the token.
        // For a more complete server-side logout, one could implement a token blacklist.
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully.");
    }

    private void sendVerificationEmail(String userEmail, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sarvasya.edu.tech@gmail.com");
        message.setTo("sarvasya.edu.tech@gmail.com"); // Routing all sarvasya-admin verifications to central mail
        message.setSubject("New Sarvasya Admin Verification Request");
        message.setText("A new sarvasya-admin account has been created: " + userEmail + "\n\n" +
                        "Please click the link below to verify and activate this account:\n" +
                        verificationLink + "\n\n" +
                        "If you did not expect this, please ignore this email.");
        mailSender.send(message);
    }
}
