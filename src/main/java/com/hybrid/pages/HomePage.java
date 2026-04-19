package com.hybrid.pages;

import com.hybrid.base.BasePage;
import com.hybrid.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Models the OrangeHRM post-login home/dashboard page.
 * Pattern Used: Page Object Model, because page structure and behavior should be represented as a reusable object.
 * Used By     : LoginTest and HomeTest.
 * How to Extend: Add locators and methods for menus, cards, widgets, or navigation links found on the home page.
 */
public class HomePage extends BasePage {

    private static final By DASHBOARD_HEADER_LOCATOR = By.cssSelector(
            "h6.oxd-topbar-header-breadcrumb-module, h6.oxd-text.oxd-text--h6.oxd-topbar-header-breadcrumb-module"
    );

    private static final By USER_DROPDOWN_LOCATOR = By.cssSelector(
            "p.oxd-userdropdown-name, span.oxd-userdropdown-tab"
    );

    @FindBy(xpath = "//h6[text()='Dashboard']")
    private WebElement dashboardHeader;

    @FindBy(css = "span.oxd-userdropdown-tab")
    private WebElement userDropdown;

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Creates the HomePage object and initializes its web elements.
     * @param driver : The WebDriver instance controlling the current browser.
     * @return      : A ready-to-use HomePage object.
     * Why this way : Each page object shares the same browser session so navigation state is preserved.
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Checks whether the dashboard header becomes visible.
     * @param none   : This method does not accept parameters.
     * @return      : true if the Dashboard heading is visible, otherwise false.
     * Why this way : The dashboard heading is a strong indicator that login succeeded and the page finished loading.
     */
    public boolean isDashboardHeaderDisplayed() {
        try {
            WaitUtils.waitForUrlContains("/dashboard");
            return WaitUtils.waitForPresence(DASHBOARD_HEADER_LOCATOR).isDisplayed()
                    || WaitUtils.waitForPresence(USER_DROPDOWN_LOCATOR).isDisplayed()
                    || isDisplayed(dashboardHeader)
                    || isDisplayed(userDropdown);
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the dashboard header text.
     * @param none   : This method does not accept parameters.
     * @return      : The header text shown on the dashboard page.
     * Why this way : Text assertions are more meaningful than checking raw locators alone.
     */
    public String getDashboardHeaderText() {
        return getText(dashboardHeader);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Checks whether the user dropdown in the top bar is visible.
     * @param none   : This method does not accept parameters.
     * @return      : true when the user dropdown is displayed, otherwise false.
     * Why this way : The user dropdown is another reliable post-login element for smoke verification.
     */
    public boolean isUserDropdownDisplayed() {
        return isDisplayed(userDropdown);
    }
}
