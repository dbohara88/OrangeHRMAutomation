package com.hybrid.base;

import com.aventstack.extentreports.Status;
import com.hybrid.constants.FrameworkConstants;
import com.hybrid.driver.BrowserFactory;
import com.hybrid.driver.DriverManager;
import com.hybrid.listeners.TestListener;
import com.hybrid.reports.ExtentReportManager;
import com.hybrid.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Handles suite setup, test setup, browser initialization, and cleanup for all test classes.
 * Pattern Used: Template base test pattern, because common test lifecycle behavior should be inherited instead of repeated.
 * Used By     : LoginTest and HomeTest.
 * How to Extend: Keep shared lifecycle logic here and add new reusable helper methods for all tests when needed.
 */
@Listeners(TestListener.class)
public class BaseTest {

    protected final Logger logger = LogManager.getLogger(getClass());

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Initializes the ExtentReports object once before the suite starts.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : Reports should be initialized once per suite so every test writes into one consolidated HTML file.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ExtentReportManager.initializeReport();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Creates a driver for the requested browser, configures it, and opens the base URL.
     * @param browser : Browser name from testng.xml or command line override.
     * @return      : Nothing.
     * Why this way : Using @Parameters keeps browser selection externalized, which is essential for cross-browser and CI execution.
     */
    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser) {
        String resolvedBrowser = resolveBrowser(browser);

        DriverManager.setDriver(BrowserFactory.createDriver(resolvedBrowser));

        WebDriver driver = DriverManager.getDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getIntProperty("page.load.timeout"))
        );
        driver.manage().timeouts().implicitlyWait(Duration.ZERO); // Hybrid frameworks prefer explicit waits over implicit waits.

        logger.info("Launching browser: {}", resolvedBrowser);
        driver.get(ConfigReader.getProperty("base.url"));

        // Logging to Extent after the listener creates the test node keeps the report beginner-friendly.
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.INFO, "Opened application URL: "
                    + ConfigReader.getProperty("base.url"));
            ExtentReportManager.getTest().log(Status.INFO, "Browser used: " + resolvedBrowser);
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Quits the current browser and removes the driver from ThreadLocal storage.
     * @param result : The current test result, available for future teardown extensions.
     * @return      : Nothing.
     * Why this way : DriverManager.removeDriver() prevents memory leaks by quitting the browser and clearing thread state.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        logger.info("Closing browser for test: {}", result.getMethod().getMethodName());
        DriverManager.removeDriver();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Flushes the ExtentReports output once the suite has finished executing.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : Flushing at suite end guarantees the final HTML report contains all test logs.
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentReportManager.flushReport();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the active thread's WebDriver for child test classes.
     * @param none   : This method does not accept parameters.
     * @return      : Current thread's WebDriver instance.
     * Why this way : Tests and page objects sometimes need direct driver access for advanced assertions or navigation.
     */
    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Resolves the final browser choice using command line, TestNG parameter, and config fallback.
     * @param suiteBrowser : The browser value provided by TestNG from the suite file.
     * @return      : Final browser string to use for driver creation.
     * Why this way : Command-line overrides are useful in Jenkins, while config fallback keeps the framework usable outside suite files.
     */
    private String resolveBrowser(String suiteBrowser) {
        String commandLineBrowser = System.getProperty("browser");

        if (commandLineBrowser != null && !commandLineBrowser.trim().isEmpty()) {
            return commandLineBrowser.trim().toLowerCase();
        }

        if (suiteBrowser != null && !suiteBrowser.trim().isEmpty()) {
            return suiteBrowser.trim().toLowerCase();
        }

        return ConfigReader.getProperty("browser", FrameworkConstants.DEFAULT_BROWSER).toLowerCase();
    }
}
