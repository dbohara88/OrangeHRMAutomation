package com.hybrid.tests;

import com.aventstack.extentreports.Status;
import com.hybrid.base.BaseTest;
import com.hybrid.pages.HomePage;
import com.hybrid.pages.LoginPage;
import com.hybrid.reports.ExtentReportManager;
import com.hybrid.utils.ConfigReader;
import com.hybrid.utils.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Executes data-driven login validation scenarios using Excel-backed test data.
 * Pattern Used: Data-Driven Testing + Page Object Model, because the same test logic should run against multiple datasets cleanly.
 * Used By     : TestNG suite files during smoke/regression/cross-browser execution.
 * How to Extend: Add more rows in Excel or add more login-focused test methods that reuse the same page objects and helpers.
 */
public class LoginTest extends BaseTest {

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Supplies login test data from the Excel workbook to the login test method.
     * @param none   : This method does not accept parameters.
     * @return      : Object[][] containing username, password, and expected result values.
     * Why this way : `parallel=true` allows TestNG to feed multiple data rows concurrently for faster execution.
     */
    @DataProvider(name = "loginData", parallel = true)
    public Object[][] loginDataProvider() {
        return ExcelUtils.getTestData("Login");
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Verifies successful and unsuccessful login behavior using Excel data rows.
     * @param username       : Username read from Excel.
     * @param password       : Password read from Excel.
     * @param expectedResult : Expected outcome such as Pass or Fail.
     * @return      : Nothing.
     * Why this way : A single reusable test keeps login validation concise while data controls the coverage breadth.
     */
    @Test(dataProvider = "loginData", description = "Validate login flow with multiple credential combinations.")
    public void verifyLoginFunctionality(String username, String password, String expectedResult) {
        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be visible before attempting login.");

        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.INFO,
                    "Executing login test with username: '" + username + "' and expected result: " + expectedResult);
        }

        String resolvedUsername = resolveDemoUsername(username, expectedResult);
        String resolvedPassword = resolveDemoPassword(password, expectedResult);

        HomePage homePage = loginPage.loginToApplication(resolvedUsername, resolvedPassword);

        if ("Pass".equalsIgnoreCase(expectedResult)) {
            Assert.assertTrue(homePage.isDashboardHeaderDisplayed(),
                    "Expected successful login, but dashboard was not displayed. Current URL: "
                            + getDriver().getCurrentUrl() + ", Title: " + getDriver().getTitle());
            Assert.assertEquals(homePage.getDashboardHeaderText(), "Dashboard",
                    "Dashboard header text should match after successful login.");
        } else {
            boolean failureVisible = loginPage.isInvalidCredentialsMessageDisplayed()
                    || loginPage.isValidationMessageDisplayed();

            Assert.assertTrue(failureVisible,
                    "Expected a login failure message, but no error or validation message was displayed. Current URL: "
                            + getDriver().getCurrentUrl() + ", Title: " + getDriver().getTitle());

            String combinedMessage = loginPage.isInvalidCredentialsMessageDisplayed()
                    ? loginPage.getInvalidCredentialsMessage()
                    : loginPage.getValidationMessageText();

            Assert.assertTrue(
                    combinedMessage.equalsIgnoreCase("Invalid credentials")
                            || combinedMessage.equalsIgnoreCase("Required"),
                    "Unexpected login failure message: " + combinedMessage
            );
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Maps the sample tutorial username to the real OrangeHRM demo username when success is expected.
     * @param username       : Username coming from Excel.
     * @param expectedResult : Expected Pass/Fail outcome for the row.
     * @return      : Real application username when pass row alias is used; otherwise the original username.
     * Why this way : The exercise requires tutorial-friendly sample data, but the live demo site only accepts its own credentials.
     */
    private String resolveDemoUsername(String username, String expectedResult) {
        String sampleAlias = ConfigReader.getProperty("sample.valid.username");

        if ("Pass".equalsIgnoreCase(expectedResult) && sampleAlias.equals(username)) {
            return ConfigReader.getProperty("app.valid.username");
        }

        return username;
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Maps the sample tutorial password to the real OrangeHRM demo password when success is expected.
     * @param password       : Password coming from Excel.
     * @param expectedResult : Expected Pass/Fail outcome for the row.
     * @return      : Real application password when pass row alias is used; otherwise the original password.
     * Why this way : This keeps the sample Excel data readable for training while still allowing a real successful login.
     */
    private String resolveDemoPassword(String password, String expectedResult) {
        String sampleAlias = ConfigReader.getProperty("sample.valid.password");

        if ("Pass".equalsIgnoreCase(expectedResult) && sampleAlias.equals(password)) {
            return ConfigReader.getProperty("app.valid.password");
        }

        return password;
    }
}
