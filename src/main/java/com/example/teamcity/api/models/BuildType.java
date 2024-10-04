package com.example.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuildType {
    private String id;
    private String name;

    @JsonProperty("project")
    private ProjectRef projectRef;
    private Templates templates;
    private Parameters parameters;
    private Steps steps;

    @Data
    @Builder
    public static class ProjectRef {
        @JsonProperty("id")
        private String projectId;
    }

    @Data
    @Builder
    public static class Templates {
        private List<BuildTypeRef> buildType;

        @Data
        @Builder
        public static class BuildTypeRef {
            private String id;
        }
    }

    @Data
    @Builder
    public static class Parameters {
        private List<Property> property;
    }

    @Data
    @Builder
    public static class Steps {
        private List<Step> step;

        @Data
        @Builder
        public static class Step {
            private String name;
            private String type;
            private Properties properties;

            // Custom builder method for properties
            public static class StepBuilder {
                public StepBuilder properties(Properties properties) {
                    this.properties = properties;
                    return this;
                }
            }

            @Data
            @Builder
            public static class Properties {
                private List<Property> property;
            }
        }
    }

    public static BuildType createRandomBuildType() {
        Faker faker = new Faker();

        return BuildType.builder()
                .id(faker.ancient().hero())
                .name(faker.cat().name())

                // Templates
                .templates(Templates.builder()
                        .buildType(new ArrayList<>(of(
                                Templates.BuildTypeRef.builder()
                                        .id("MyTemplateID")
                                        .build()))).build())
                // Parameters
                .parameters(Parameters.builder()
                        .property(new ArrayList<>(of(
                                Property.builder()
                                        .name("myBuildParameter")
                                        .value("myValue")
                                        .build()))).build())
                // Steps
                .steps(Steps.builder()
                        .step(new ArrayList<>(of(
                                Steps.Step.builder()
                                        .name("myCommandLineStep")
                                        .type("simpleRunner")
                                        .properties(Steps.Step.Properties.builder()
                                                .property(new ArrayList<>(of(
                                                        Property.builder()
                                                                .name("script.content")
                                                                .value("echo 'Hello World from Automation testing!'")
                                                                .build()))).build()).build()))).build()).build();
    }
}
