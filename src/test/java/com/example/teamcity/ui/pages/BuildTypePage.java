package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.pages.admin.CreateBasePage;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.xpath;

@Getter
public class BuildTypePage extends CreateBasePage {
    private static final String BUILDTYPE_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=createBuildTypeMenu";

    private final SelenideElement title = $(xpath("//*[text() = 'Create Build Configuration']"));
    private final SelenideElement buildTypeName = $("#buildTypeName");

    public static BuildTypePage open(String projectId) {
        return Selenide.open(BUILDTYPE_URL.formatted(projectId), BuildTypePage.class);
    }

    public void createBuildType(String repoUrl, String buildTypeName) {
        title.should(visible);
        baseCreateForm(repoUrl);
        this.buildTypeName.val(buildTypeName);
        submitButton.click();
    }
}