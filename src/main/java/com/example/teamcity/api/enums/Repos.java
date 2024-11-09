package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Repos {
    GITHUB_SPRING_CORE("https://github.com/AlexPshe/spring-core-for-qa");

    private final String repo;
}
