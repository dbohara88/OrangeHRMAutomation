package com.hybrid.base;

import com.hybrid.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Serves as the parent Page Object class containing reusable Selenium actions.
 * Pattern Used: Page Object Model base class, because common page behavior should live in one reusable layer.
 * Used By     : LoginPage, HomePage, and any future page classes.
 * How to Extend: Add only generic page actions here, and keep page-specific locators and business flows in child page classes.
 */
public class BasePage {

    protected WebDriver driver;

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Creates a page object and initializes its PageFactory web elements.
     * @param driver : The WebDriver instance controlling the current browser.
     * @return      : A fully initialized page object instance.
     * Why this way : We use PageFactory because it lazily initializes web elements, reducing NoSuchElementException.
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Clicks an element after waiting for it to become clickable.
     * @param element : The WebElement to click.
     * @return      : Nothing.
     * Why this way : Waiting before clicking makes tests more stable against slow UI rendering and animations.
     */
    public void click(WebElement element) {
        WaitUtils.waitForClickability(element).click();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Clears an input field and types the supplied text into it.
     * @param element : The text box or input field to update.
     * @param text    : The value to type into the field.
     * @return      : Nothing.
     * Why this way : Clearing first prevents old text from mixing with new test data and causing false failures.
     */
    public void type(WebElement element, String text) {
        WebElement visibleElement = WaitUtils.waitForVisibility(element);
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the visible text of an element after waiting for it.
     * @param element : The WebElement whose text should be read.
     * @return      : Visible text content from the element.
     * Why this way : Waiting avoids reading text too early before the UI has finished updating.
     */
    public String getText(WebElement element) {
        return WaitUtils.waitForVisibility(element).getText().trim();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Checks whether an element is displayed to the user.
     * @param element : The WebElement to verify.
     * @return      : true if the element becomes visible, otherwise false.
     * Why this way : Wrapping visibility logic in try/catch keeps page methods clean and easy to reuse in assertions.
     */
    public boolean isDisplayed(WebElement element) {
        try {
            return WaitUtils.fluentWaitForVisibility(element).isDisplayed();
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Waits until an element is visible and returns it.
     * @param element : The WebElement to wait for.
     * @return      : The same element after it becomes visible.
     * Why this way : This gives child pages a readable helper for synchronization without exposing wait implementation details.
     */
    public WebElement waitForElement(WebElement element) {
        return WaitUtils.waitForVisibility(element);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the current browser page title.
     * @param none   : This method does not accept parameters.
     * @return      : The current page title string.
     * Why this way : Title checks are sometimes useful for lightweight navigation validation.
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the current browser URL.
     * @param none   : This method does not accept parameters.
     * @return      : The full URL currently open in the browser.
     * Why this way : URL checks complement element-based assertions when validating redirects.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
