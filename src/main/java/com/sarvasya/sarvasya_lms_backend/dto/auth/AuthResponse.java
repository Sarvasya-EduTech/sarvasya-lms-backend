package com.sarvasya.sarvasya_lms_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserDto user;

    @Getter
@Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private java.util.UUID id;
        private String email;
        private String name;
        private String role;
        private boolean requiresPasswordChange;
    }
}








