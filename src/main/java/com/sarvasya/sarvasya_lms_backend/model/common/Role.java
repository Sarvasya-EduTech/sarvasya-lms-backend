package com.sarvasya.sarvasya_lms_backend.model.common;

import com.sarvasya.sarvasya_lms_backend.model.user.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    SARVASYA_ADMIN("sarvasya-admin"),
    ADMIN("admin"),
    PROFESSOR("professor"),
    USER("user"),
    TENANT_MANAGER("tenant-manager");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}








