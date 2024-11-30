package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.ProjectElement;
import lombok.Getter;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.openqa.selenium.By.xpath;

@Getter
public class ProjectsPage extends BasePage {
    private static final String PROJECTS_URL = "/favorite/projects";

    private ElementsCollection projectElements = $$("div[class*='Subproject__container']");
    private SelenideElement spanFavoriteProjects = $("span[class='ProjectPageHeader__title--ih']");
    private SelenideElement header = $("[data-test = 'overview-header']");
    private SelenideElement search = $("[data-test = 'sidebar-search']");
    private SelenideElement editProject = $(xpath("//span[contains(text(),'Edit project')]"));

    public SelenideElement getProjectSideBarTitle(String projectName) {
        return $(xpath(String.format("(//span[text() = '%s'])[1]", projectName)));
    }

    // ElementCollection -> List<ProjectElement>
    // UI elements -> List<Object>
    // ElementCollection -> List<BasePageElement>

    public static ProjectsPage open() {
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    public ProjectsPage() {
        search.shouldBe(Condition.visible, BASE_WAITING);
    }

    public void findAndOpenProject(String projectName) {
        search.shouldBe(Condition.visible, BASE_WAITING);
//        search.val(projectName);
        getProjectSideBarTitle(projectName).click();
        header.shouldBe(Condition.visible, BASE_WAITING);
    }

    public List<ProjectElement> getProjects() {
        return generatePageElements(projectElements, ProjectElement::new);
    }
}