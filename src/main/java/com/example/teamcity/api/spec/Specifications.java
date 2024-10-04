package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static com.example.teamcity.api.config.Config.getProperty;

public class Specifications {
    private static Specifications spec;

    private Specifications() {
    }

    public static Specifications getSpec() {
        if (spec == null) {
            spec = new Specifications();
        }
        return spec;
    }

    private RequestSpecBuilder requestBuilder() {
        var requestBuilder = new RequestSpecBuilder();
        requestBuilder.addFilter(new RequestLoggingFilter());
        requestBuilder.addFilter(new ResponseLoggingFilter());

        return requestBuilder;
    }

    public RequestSpecification unauthSpec() {
        var requestBuilder = requestBuilder();
        requestBuilder.setContentType(ContentType.JSON);
        requestBuilder.setAccept(ContentType.JSON);
        return requestBuilder.build();
    }

    public RequestSpecification authSpec(User user) {
        var requestBuilder = requestBuilder();
        requestBuilder
                .setBaseUri("http://" + user.getUsername() + ":" + user.getPassword() + "@" + getProperty("host"));
        return requestBuilder.build();
    }

    public RequestSpecification createUserSpec(User user) {
        var requestBuilder = requestBuilder();
        requestBuilder
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(
                        "http://" + Config.getProperty("admin_username") + ":" + Config.getProperty("admin_password")
                                + "@" + getProperty("host") + "/app/rest/users")
                .setBody(user);
        return requestBuilder.build();
    }

    public RequestSpecification createProjectByUserSpec(User user, Project project) {
        var requestBuilder = requestBuilder();
        requestBuilder
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(
                        "http://" + user.getUsername().toLowerCase() + ":" + user.getPassword().toLowerCase()
                                + "@" + getProperty("host") + "/app/rest/projects")
                .setBody(project);
        return requestBuilder.build();
    }

    public RequestSpecification createBuildTypeForProjectByUserSpec(User user, Project project, BuildType buildType) {
        var requestBuilder = requestBuilder();
        buildType.setProjectRef(BuildType.ProjectRef.builder().projectId(project.getId()).build());

        requestBuilder
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setBaseUri(
                        "http://" + user.getUsername().toLowerCase() + ":" + user.getPassword().toLowerCase()
                                + "@" + getProperty("host") + "/app/rest/buildTypes")
                .setBody(buildType);
        return requestBuilder.build();
    }
}