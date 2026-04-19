package com.hybrid.driver;

import org.openqa.selenium.WebDriver;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Manages WebDriver instances safely for parallel test execution.
 * Pattern Used: Singleton-style Driver Manager with ThreadLocal, because each test thread needs its own isolated driver.
 * Used By     : BaseTest, WaitUtils, ScreenshotUtils, page classes, and listeners.
 * How to Extend: Keep methods static, and add helper methods only if they still preserve one-driver-per-thread behavior.
 */
public final class DriverManager {

    /*
        ThreadLocal gives each thread its own WebDriver instance.
        Without it, parallel tests would share one browser and interfere with each other.
        That causes flaky failures such as one test clicking on another test's page.
     */
    private static final ThreadLocal<WebDriver> DRIVER_THREAD = new ThreadLocal<>();

    private DriverManager() {
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the WebDriver assigned to the current thread.
     * @param none   : This method does not accept parameters.
     * @return      : The current thread's WebDriver instance, or null if not yet set.
     * Why this way : Static access keeps driver retrieval simple from utilities without passing driver references everywhere.
     */
    public static WebDriver getDriver() {
        return DRIVER_THREAD.get();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Stores a WebDriver instance for the current execution thread.
     * @param driver : The WebDriver created for this test thread.
     * @return      : Nothing.
     * Why this way : Setting the driver into ThreadLocal isolates browser sessions across parallel tests.
     */
    public static void setDriver(WebDriver driver) {
        DRIVER_THREAD.set(driver);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Quits and removes the current thread's WebDriver instance.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : We quit the browser and then remove the ThreadLocal reference to prevent memory leaks
     *                and stale driver objects from surviving between tests.
     */
    public static void removeDriver() {
        WebDriver driver = getDriver();

        if (driver != null) {
            driver.quit(); // Closing the browser frees OS resources such as processes and memory.
            DRIVER_THREAD.remove(); // Removing the reference is important for long-running parallel suites.
        }
    }
}
