package com.hybrid.tests;

import com.aventstack.extentreports.Status;
import com.hybrid.base.BaseTest;
import com.hybrid.pages.HomePage;
import com.hybrid.pages.LoginPage;
import com.hybrid.reports.ExtentReportManager;
import com.hybrid.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Provides a simple smoke test for the OrangeHRM home/dashboard page after login.
 * Pattern Used: Page Object Model, because tests should call page methods instead of direct locators.
 * Used By     : TestNG suite files for smoke and regression validation.
 * How to Extend: Add more dashboard assertions or create additional smoke tests for navigation cards and menus.
 */
public class HomeTest extends BaseTest {

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Logs into OrangeHRM using valid credentials and verifies the dashboard is visible.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : A clean smoke test gives quick confidence that the core login-to-home journey still works.
     */
    @Test(description = "Smoke test to verify dashboard home page after valid login.")
    public void verifyHomePageAfterSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(getDriver());
        HomePage homePage = loginPage.loginToApplication(
                ConfigReader.getProperty("app.valid.username"),
                ConfigReader.getProperty("app.valid.password")
        );

        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.INFO, "Logged in using configured demo credentials.");
        }

        Assert.assertTrue(homePage.isDashboardHeaderDisplayed(),
                "Dashboard header should be visible after successful login. Current URL: "
                        + getDriver().getCurrentUrl() + ", Title: " + getDriver().getTitle());
        Assert.assertTrue(homePage.isUserDropdownDisplayed(),
                "User dropdown should be visible after successful login.");
    }
}
