package com.hybrid.utils;

import com.hybrid.constants.FrameworkConstants;
import com.hybrid.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Wraps explicit and fluent wait logic into readable reusable utility methods.
 * Pattern Used: Utility Wrapper pattern, because wait logic is reused across many pages and tests.
 * Used By     : BasePage and any test or utility that needs synchronized element interaction.
 * How to Extend: Add new wait methods for visibility, alerts, frames, JavaScript readiness, or URL conditions as needed.
 */
public final class WaitUtils {

    private WaitUtils() {
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Waits until the given element becomes visible on the page.
     * @param element : The WebElement that should appear and become visible.
     * @return      : The same WebElement after it becomes visible.
     * Why this way : WebDriverWait waits only as long as needed up to the timeout, which is more efficient
     *                and more stable than hardcoded sleeps.
     */
    public static WebElement waitForVisibility(WebElement element) {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(ConfigReader.getIntProperty("explicit.wait"))
        ).until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Waits until the given element is clickable.
     * @param element : The WebElement that should be ready for clicking.
     * @return      : The same WebElement after it becomes clickable.
     * Why this way : Clickable state reduces common Selenium issues where an element exists but is not yet interactable.
     */
    public static WebElement waitForClickability(WebElement element) {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(ConfigReader.getIntProperty("explicit.wait"))
        ).until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Waits until an element located by a By locator is present in the DOM.
     * @param locator : The Selenium locator describing the target element.
     * @return      : The found WebElement once present.
     * Why this way : By-based waits are useful when a page object wants to wait before PageFactory-backed fields are used.
     */
    public static WebElement waitForPresence(By locator) {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(ConfigReader.getIntProperty("explicit.wait"))
        ).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Waits using FluentWait for an element to become visible.
     * @param element : The WebElement that should become visible.
     * @return      : The same WebElement after it becomes visible.
     * Why this way : We use FluentWait here instead of Thread.sleep because FluentWait polls at intervals
     *                and respects dynamic elements, while Thread.sleep blocks the thread wastefully.
     */
    public static WebElement fluentWaitForVisibility(WebElement element) {
        return new FluentWait<>(DriverManager.getDriver())
                .withTimeout(Duration.ofSeconds(ConfigReader.getIntProperty("explicit.wait")))
                .pollingEvery(Duration.ofSeconds(FrameworkConstants.DEFAULT_POLLING_WAIT))
                .ignoring(NoSuchElementException.class)
                .until(driver -> element.isDisplayed() ? element : null);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Waits until the current page URL contains a given text fragment.
     * @param urlFraction : A meaningful part of the URL expected after navigation.
     * @return      : true when the expected URL fragment appears before timeout.
     * Why this way : URL checks are helpful for navigation validation without depending only on visible elements.
     */
    public static boolean waitForUrlContains(String urlFraction) {
        return new WebDriverWait(
                DriverManager.getDriver(),
                Duration.ofSeconds(ConfigReader.getIntProperty("explicit.wait"))
        ).until(ExpectedConditions.urlContains(urlFraction));
    }
}
