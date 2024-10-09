package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.enums.UserRoles.PROJECT_ADMIN;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static com.example.teamcity.api.spec.Specifications.authSpec;
import static java.util.Collections.singletonList;

public class BuildTypeTest extends BaseApiTest {

    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(singletonList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        new UncheckedBase(authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(testData.getBuildType().getId())));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "Roles"})
    public void projectAdminCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        testData.getUser().setRoles(generate(Roles.class, PROJECT_ADMIN.getRole(), "p:" + testData.getProject().getId()));
        superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(authSpec(testData.getUser()));
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Build type name is not correct");
    }

    @Test(description = "Project admin should not be able to create build type for not their project", groups = {"Negative", "Roles"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        Project project2 = superUserCheckRequests.<Project>getRequest(PROJECTS).create(generate(Project.class));

        testData.getUser().setRoles(generate(Roles.class, PROJECT_ADMIN.getRole(), "p:" + testData.getProject().getId()));
        superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser());

        testData.getBuildType().setProject(project2);

        var response = new UncheckedBase(authSpec(testData.getUser()), BUILD_TYPES)
                .create(testData.getBuildType());

        softy.assertEquals(response.getStatusCode(), HttpStatus.SC_FORBIDDEN, "Expected status code 403 Forbidden");

        softy.assertTrue(response.getBody().asString().contains("You do not have enough permissions to edit project"),
                "Access denied. Check the user has enough permissions to perform the operation.");
    }
}