package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoles {
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    PROJECT_ADMIN("PROJECT_ADMIN");

    private final String role;
}
