package com.example.teamcity.api.models;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

import static java.util.List.of;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String password;
    private String email;
    private UserRoles roles;

    @Data
    @Builder
    public static class UserRoles {
        private ArrayList<Role> role;
    }

    @Data
    @Builder
    public static class Role {
        private String roleId;
        private String scope;
    }

    public static User createRandomUser() {
        Faker faker = new Faker();
        return User.builder()
                .username(faker.name().firstName().toLowerCase())
                .password(faker.name().lastName().toLowerCase())
                .email(faker.internet().emailAddress())
                .roles(new UserRoles(new ArrayList<>(of(new Role("SYSTEM_ADMIN", "g")))))
                .build();
    }
}