package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Roles;
import com.example.teamcity.api.utils.Utils;
import com.example.teamcity.ui.pages.BuildTypePage;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.text;
import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.enums.Repos.GITHUB_SPRING_CORE;
import static com.example.teamcity.api.enums.UserRoles.PROJECT_ADMIN;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    BuildTypePage buildTypePage = new BuildTypePage();

    @Test(description = "User should be able to create a project", groups = "Positive")
    public void userCreateProject() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        loginAs(testData.getUser());

        // взаимодействие с UI
        CreateProjectPage.open("_Root")
                .createForm(GITHUB_SPRING_CORE.getRepo())
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS).read("name:" + testData.getProject().getName());
        softy.assertNotNull(createdProject);

        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()));

        softy.assertTrue(foundProjects);
    }

    @Test(description = "User should be able to create project and build type", groups = {"Positive"})
    public void userCreatesBuildType() {
        String buildTypeName = testData.getBuildType().getName();
        String projectId = testData.getProject().getId();

        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        loginAs(testData.getUser());

        BuildTypePage.open(projectId);
        buildTypePage.createBuildType(GITHUB_SPRING_CORE.getRepo(), buildTypeName);

        buildTypePage.getCreateSuccessfulMessage().should(Condition.visible);
        buildTypePage.getCreateSuccessfulMessage().shouldHave(
                text(("New build configuration \"%s\" and VCS root \"%s\" have been successfully created.")
                        .formatted(buildTypeName, GITHUB_SPRING_CORE.getRepo() + "#refs/heads/master")));

        superUserCheckRequests.<BuildType>getRequest(BUILD_TYPES)
                .read("id:" + Utils.createBuildTypeLocator(projectId, buildTypeName));

    }

    @Test(description = "User should NOT be able to create build type for not their project", groups = {"Negative"})
    public void userCreatesBuildTypeForNotTheirProjectTest() throws InterruptedException {
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        Project project2 = superUserCheckRequests.<Project>getRequest(PROJECTS).create(generate(Project.class));

        testData.getUser().setRoles(generate(Roles.class, PROJECT_ADMIN.getRole(), "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(Endpoint.USERS).create(testData.getUser());

        loginAs(testData.getUser());

        BuildTypePage.open(project2.getId());
        buildTypePage.getSubmitButton().shouldHave(Condition.disabled);
        buildTypePage.getUrlInput().shouldHave(Condition.disabled);
    }

    @Test(description = "User should NOT be able to create build type with empty name", groups = {"Negative"})
    public void userCreatesBuildTypeWithEmptyNameTest() {
        final String EMPTY_STRING = "";
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());
        superUserCheckRequests.getRequest(Endpoint.USERS).create(testData.getUser());

        loginAs(testData.getUser());
        BuildTypePage.open(testData.getProject().getId());

        buildTypePage.createBuildType(GITHUB_SPRING_CORE.getRepo(), EMPTY_STRING);
        buildTypePage.getEmptyBuildTypeNameMessage().shouldHave(Condition.visible);
    }
}