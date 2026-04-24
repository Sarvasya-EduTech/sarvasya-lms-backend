package com.sarvasya.sarvasya_lms_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String role;

    private String tenantId;

    private String classId;

    private String departmentId;

    private String degreeId;
}








