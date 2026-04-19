package com.hybrid.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.hybrid.constants.FrameworkConstants;
import com.hybrid.utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Creates and manages the ExtentReports reporting objects used across the suite.
 * Pattern Used: Singleton-style report manager with ThreadLocal test nodes, because one suite shares one report while parallel tests need isolated log entries.
 * Used By     : BaseTest and TestListener.
 * How to Extend: Add reporters, system info, or custom dashboards here without touching test classes.
 */
public final class ExtentReportManager {

    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Initializes the suite-level ExtentReports instance once.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : Creating one report per suite avoids fragmented reports and keeps all test results in a single HTML artifact.
     */
    public static synchronized void initializeReport() {
        if (extentReports != null) {
            return; // Guard clause prevents duplicate initialization when multiple classes share the same suite.
        }

        try {
            Files.createDirectories(Paths.get(FrameworkConstants.REPORTS_DIRECTORY));
        } catch (Exception exception) {
            throw new RuntimeException("Unable to create reports directory.", exception);
        }

        String timestamp = new SimpleDateFormat(FrameworkConstants.TIMESTAMP_PATTERN).format(new Date());
        String reportPath = FrameworkConstants.REPORTS_DIRECTORY + "/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setReportName("HybridQAFramework Execution Report");
        sparkReporter.config().setDocumentTitle("HybridQAFramework Report");

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);

        // Helpful environment metadata appears at the top of the report for easier debugging.
        extentReports.setSystemInfo("Environment", ConfigReader.getProperty("environment"));
        extentReports.setSystemInfo("Base URL", ConfigReader.getProperty("base.url"));
        extentReports.setSystemInfo("Headless", String.valueOf(ConfigReader.getBooleanProperty("headless")));
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Creates a new ExtentTest node for the current test method and stores it in ThreadLocal.
     * @param testName : The display name of the test method being executed.
     * @return      : The newly created ExtentTest instance.
     * Why this way : ThreadLocal keeps logs from parallel tests from writing into the wrong report node.
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest test = extentReports.createTest(testName);
        EXTENT_TEST.set(test);
        return test;
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the ExtentTest node bound to the current test thread.
     * @param none   : This method does not accept parameters.
     * @return      : Current thread's ExtentTest instance.
     * Why this way : Centralized retrieval keeps logging code clean in listeners and tests.
     */
    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Removes the current thread's ExtentTest reference.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : Removing thread-bound report objects prevents stale references between parallel test executions.
     */
    public static void unloadTest() {
        EXTENT_TEST.remove();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Flushes the report so all logged test results are written to the HTML file.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing.
     * Why this way : ExtentReports writes buffered data during flush, so calling it at suite end guarantees a complete report file.
     */
    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
