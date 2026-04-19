package com.hybrid.driver;

import com.hybrid.constants.FrameworkConstants;
import com.hybrid.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Creates browser-specific WebDriver instances based on the requested browser name.
 * Pattern Used: Factory Pattern, because object creation logic for Chrome, Firefox, and Edge is centralized in one place.
 * Used By     : BaseTest.
 * How to Extend: Add another browser branch, configure its options, and return the corresponding driver instance.
 */
public final class BrowserFactory {

    private BrowserFactory() {
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Creates and returns a WebDriver for the requested browser.
     * @param browserName : Browser name such as chrome, firefox, or edge.
     * @return      : A ready-to-use WebDriver instance for the selected browser.
     * Why this way : A switch-case is easy for beginners to read and extend, while keeping all driver setup logic in one file.
     */
    public static WebDriver createDriver(String browserName) {
        String normalizedBrowser = browserName == null || browserName.trim().isEmpty()
                ? FrameworkConstants.DEFAULT_BROWSER
                : browserName.trim().toLowerCase();

        switch (normalizedBrowser) {
            case "firefox":
                return createFirefoxDriver();
            case "edge":
                return createEdgeDriver();
            case "chrome":
            default:
                return createChromeDriver();
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Builds a ChromeDriver with framework-standard options.
     * @param none   : This method does not accept parameters.
     * @return      : Configured ChromeDriver instance.
     * Why this way : WebDriverManager eliminates the pain of manually matching driver versions to browser versions.
     */
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        chromeOptions.setAcceptInsecureCerts(true);
        chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--ignore-certificate-errors");

        // When local debugging is needed, `-Dheadless=false` removes the headless flag at runtime.
        if (!isHeadlessEnabled()) {
            chromeOptions = new ChromeOptions();
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            chromeOptions.setAcceptInsecureCerts(true);
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--window-size=1920,1080");
            chromeOptions.addArguments("--ignore-certificate-errors");
        }

        return new ChromeDriver(chromeOptions);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Builds a FirefoxDriver with framework-standard options.
     * @param none   : This method does not accept parameters.
     * @return      : Configured FirefoxDriver instance.
     * Why this way : Keeping browser-specific options near driver creation avoids hidden setup behavior elsewhere.
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        firefoxOptions.setAcceptInsecureCerts(true);
        firefoxOptions.addArguments("--no-sandbox");
        firefoxOptions.addArguments("--disable-dev-shm-usage");
        firefoxOptions.addArguments("--width=1920");
        firefoxOptions.addArguments("--height=1080");

        if (isHeadlessEnabled()) {
            firefoxOptions.addArguments("--headless");
        }

        return new FirefoxDriver(firefoxOptions);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Builds an EdgeDriver with framework-standard options.
     * @param none   : This method does not accept parameters.
     * @return      : Configured EdgeDriver instance.
     * Why this way : Edge is Chromium-based, so its options are similar to Chrome and easy to keep aligned.
     */
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();

        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        edgeOptions.setAcceptInsecureCerts(true);
        edgeOptions.addArguments("--headless=new");
        edgeOptions.addArguments("--no-sandbox");
        edgeOptions.addArguments("--disable-dev-shm-usage");
        edgeOptions.addArguments("--disable-gpu");
        edgeOptions.addArguments("--window-size=1920,1080");
        edgeOptions.addArguments("--ignore-certificate-errors");

        if (!isHeadlessEnabled()) {
            edgeOptions = new EdgeOptions();
            edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            edgeOptions.setAcceptInsecureCerts(true);
            edgeOptions.addArguments("--no-sandbox");
            edgeOptions.addArguments("--disable-dev-shm-usage");
            edgeOptions.addArguments("--disable-gpu");
            edgeOptions.addArguments("--window-size=1920,1080");
            edgeOptions.addArguments("--ignore-certificate-errors");
        }

        return new EdgeDriver(edgeOptions);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Resolves whether browsers should run in headless mode.
     * @param none   : This method does not accept parameters.
     * @return      : true when headless mode should be enabled, otherwise false.
     * Why this way : System properties let Jenkins or local engineers override config values without editing files.
     */
    private static boolean isHeadlessEnabled() {
        return Boolean.parseBoolean(System.getProperty(
                "headless",
                ConfigReader.getProperty("headless", "true")
        ));
    }
}
