package com.hybrid.pages;

import com.hybrid.base.BasePage;
import com.hybrid.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Models the OrangeHRM login page and exposes reusable login-related actions.
 * Pattern Used: Page Object Model, because UI locators and interactions should be separated from test logic.
 * Used By     : LoginTest and HomeTest.
 * How to Extend: Add new locators and methods only for this page, such as forgot-password or social login actions.
 */
public class LoginPage extends BasePage {

    private static final By INVALID_CREDENTIALS_LOCATOR = By.cssSelector(
            ".oxd-alert-content-text, p.oxd-text.oxd-text--p.oxd-alert-content-text"
    );

    private static final By VALIDATION_MESSAGE_LOCATOR = By.cssSelector("span.oxd-input-field-error-message");

    @FindBy(name = "username")
    private WebElement usernameTextBox;

    @FindBy(name = "password")
    private WebElement passwordTextBox;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(css = ".oxd-alert-content-text")
    private WebElement invalidCredentialsMessage;

    @FindBy(css = "span.oxd-input-field-error-message")
    private List<WebElement> validationMessages;

    @FindBy(xpath = "//h5[text()='Login']")
    private WebElement loginPageHeading;

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Creates the LoginPage object and initializes its web elements.
     * @param driver : The WebDriver instance controlling the current browser.
     * @return      : A ready-to-use LoginPage object.
     * Why this way : Each page object receives the shared driver so it operates in the same browser session.
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Enters the provided username into the username field.
     * @param username : The username value to type.
     * @return      : Nothing.
     * Why this way : Page methods should represent user actions clearly, making tests read like business steps.
     */
    public void enterUsername(String username) {
        type(usernameTextBox, username);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Enters the provided password into the password field.
     * @param password : The password value to type.
     * @return      : Nothing.
     * Why this way : Keeping username and password actions separate makes troubleshooting failed steps easier.
     */
    public void enterPassword(String password) {
        type(passwordTextBox, password);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Clicks the login button.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : Encapsulating the button click keeps raw locators out of the test classes.
     */
    public void clickLoginButton() {
        click(loginButton);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Performs the full login action using the supplied credentials.
     * @param username : Username to enter on the login page.
     * @param password : Password to enter on the login page.
     * @return      : A HomePage object, because successful navigation usually lands on the home/dashboard screen.
     * Why this way : Returning the next page object models the natural page flow and keeps tests readable.
     */
    public HomePage loginToApplication(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        return new HomePage(driver);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Checks whether the login page heading is displayed.
     * @param none   : This method does not accept parameters.
     * @return      : true if the Login heading is visible, otherwise false.
     * Why this way : Heading presence gives us a simple smoke check that the correct page loaded.
     */
    public boolean isLoginPageDisplayed() {
        return (isDisplayed(usernameTextBox) && isDisplayed(loginButton))
                || getCurrentUrl().contains("/auth/login")
                || isDisplayed(loginPageHeading);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Reads the invalid credentials banner text.
     * @param none   : This method does not accept parameters.
     * @return      : The visible error text shown after a failed login.
     * Why this way : Tests should assert business-level messages rather than raw DOM details whenever possible.
     */
    public String getInvalidCredentialsMessage() {
        try {
            return WaitUtils.waitForPresence(INVALID_CREDENTIALS_LOCATOR).getText().trim();
        } catch (Exception exception) {
            return getText(invalidCredentialsMessage);
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Checks whether the invalid credentials banner is displayed.
     * @param none   : This method does not accept parameters.
     * @return      : true when the banner is visible, otherwise false.
     * Why this way : A separate boolean helper makes negative-path assertions more readable in tests.
     */
    public boolean isInvalidCredentialsMessageDisplayed() {
        try {
            return WaitUtils.waitForPresence(INVALID_CREDENTIALS_LOCATOR).isDisplayed();
        } catch (Exception exception) {
            return isDisplayed(invalidCredentialsMessage);
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Checks whether any required-field validation message is displayed.
     * @param none   : This method does not accept parameters.
     * @return      : true when at least one validation message is visible, otherwise false.
     * Why this way : OrangeHRM shows inline validation for empty mandatory fields, so failure checks must cover both UI patterns.
     */
    public boolean isValidationMessageDisplayed() {
        try {
            return WaitUtils.waitForPresence(VALIDATION_MESSAGE_LOCATOR).isDisplayed()
                    || (validationMessages != null && validationMessages.stream().anyMatch(WebElement::isDisplayed));
        } catch (Exception exception) {
            return validationMessages != null
                    && validationMessages.stream().anyMatch(WebElement::isDisplayed);
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the first visible validation message text, if available.
     * @param none   : This method does not accept parameters.
     * @return      : The first visible validation message, or an empty String when none is shown.
     * Why this way : Returning the first relevant message is enough for beginner-friendly failure assertions.
     */
    public String getValidationMessageText() {
        return validationMessages.stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .map(element -> element.getText().trim())
                .orElse("");
    }
}
