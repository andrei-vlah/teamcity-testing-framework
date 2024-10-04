package com.example.teamcity.api.models;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Project {
    private ParentProject parentProject;
    private String name;
    private String id;
    private boolean copyAllAssociatedSettings;

    @Builder
    @Data
    public static class ParentProject {
        private String locator;
    }

    public static Project createRandomProject() {
        Faker faker = new Faker();
        return Project.builder()
                .parentProject(new ParentProject("_Root"))
                .id(faker.ancient().titan())
                .name(faker.superhero().name())
                .copyAllAssociatedSettings(true)
                .build();
    }
}