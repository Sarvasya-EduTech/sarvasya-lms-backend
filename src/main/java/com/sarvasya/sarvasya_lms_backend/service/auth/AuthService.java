package com.sarvasya.sarvasya_lms_backend.service.auth;
import com.sarvasya.sarvasya_lms_backend.dto.auth.AuthRequest;
import com.sarvasya.sarvasya_lms_backend.dto.auth.AuthResponse;
import com.sarvasya.sarvasya_lms_backend.dto.auth.ChangePasswordRequest;
import com.sarvasya.sarvasya_lms_backend.dto.auth.SignupRequest;
import com.sarvasya.sarvasya_lms_backend.dto.common.MessageResponse;


public interface AuthService {
    MessageResponse signup(SignupRequest request);

    AuthResponse login(AuthRequest request);

    MessageResponse changePassword(String email, ChangePasswordRequest request);

    MessageResponse logout();
}









