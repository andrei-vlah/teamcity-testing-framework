package com.example.teamcity.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.example.teamcity.BaseTest;
import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.AuthModules;
import com.example.teamcity.api.models.ServerAuthSettings;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.ServerAuthRequest;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.LoginPage;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.Map;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BaseUiTest extends BaseTest {
    private final ServerAuthRequest serverAuthRequest = new ServerAuthRequest(Specifications.superUserSpec());
    private AuthModules authModules;
    private boolean perProjectPermissions;

    @BeforeSuite(alwaysRun = true)
    public void setupUiTest() {
        Configuration.browser = Config.getProperty("browser");
        Configuration.baseUrl = "http://" + Config.getProperty("host");
        // НЕТ ПИШИТЕ UI ТЕСТЫ С ЛОКАЛЬНЫМ БРАУЗЕРОМ
        // А ПОТОМ ЗАПУСКАЕТЕ НА REMOTE BROWSER
        Configuration.remote = Config.getProperty("remote");
        Configuration.browserSize = Config.getProperty("browserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true)
                .includeSelenideSteps(true));

        // Получаем текущие настройки perProjectPermissions
        perProjectPermissions = serverAuthRequest.read().getPerProjectPermissions();

        authModules = generate(AuthModules.class);
        // Обновляем значение perProjectPermissions на true
        serverAuthRequest.update(ServerAuthSettings.builder()
                .perProjectPermissions(true)
                .modules(authModules)
                .build());
    }

    @AfterMethod(alwaysRun = true)
    public void closeWebDriver() {
        Selenide.closeWebDriver();
    }

    protected void loginAs(User user) {
        LoginPage.open().login(user);
    }

    @AfterSuite(alwaysRun = true)
    public void cleanUpServerAuthSettings() {
        // Возвращаем настройке perProjectPermissions исходное значение
        serverAuthRequest.update(ServerAuthSettings.builder()
                .perProjectPermissions(perProjectPermissions)
                .modules(authModules)
                .build());
    }
}
