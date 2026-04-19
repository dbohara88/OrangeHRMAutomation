package com.hybrid.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.hybrid.reports.ExtentReportManager;
import com.hybrid.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Listens to TestNG test events and writes execution results into ExtentReports.
 * Pattern Used: Observer/Listener pattern, because TestNG publishes lifecycle events that this class reacts to.
 * Used By     : BaseTest through the @Listeners annotation.
 * How to Extend: Add custom logging, retries, defect hooks, or external notifications inside the appropriate event methods.
 */
public class TestListener implements ITestListener {

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Runs when TestNG is about to start a test context.
     * @param context : The TestNG execution context for the current test block.
     * @return      : Nothing.
     * Why this way : This hook is available for future expansion if suite-level context details need to be logged.
     */
    @Override
    public void onStart(ITestContext context) {
        // Intentionally left simple because report initialization happens once in BaseTest @BeforeSuite.
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Creates an ExtentReports node when a test method starts.
     * @param result : Metadata about the current test method.
     * @return      : Nothing.
     * Why this way : Starting a fresh report node at test start keeps logs grouped by method execution.
     */
    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportManager.createTest(result.getMethod().getMethodName())
                .log(Status.INFO, "Test execution started.");
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Marks the current test as passed in ExtentReports.
     * @param result : Metadata about the completed test method.
     * @return      : Nothing.
     * Why this way : Keeping pass/fail logging inside the listener avoids repeating report code in each test.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest().log(Status.PASS, "Test passed successfully.");
        ExtentReportManager.unloadTest();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Captures failure details, screenshot evidence, and marks the test as failed in ExtentReports.
     * @param result : Metadata about the failed test method, including the thrown exception.
     * @return      : Nothing.
     * Why this way : Centralizing screenshot capture on failure guarantees evidence is collected consistently for every test.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String screenshotPath = ScreenshotUtils.captureScreenshot(testName);
        String base64Screenshot = ScreenshotUtils.getBase64Screenshot();

        ExtentReportManager.getTest().log(Status.FAIL, result.getThrowable());
        ExtentReportManager.getTest().log(Status.FAIL, "Failure screenshot saved at: " + screenshotPath);

        try {
            ExtentReportManager.getTest().fail(
                    "Embedded failure screenshot",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot, testName).build()
            );
        } catch (Exception exception) {
            ExtentReportManager.getTest().log(Status.WARNING,
                    "Unable to embed Base64 screenshot: " + exception.getMessage());
        }

        ExtentReportManager.unloadTest();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Marks the current test as skipped in ExtentReports.
     * @param result : Metadata about the skipped test method.
     * @return      : Nothing.
     * Why this way : Skipped tests should still appear in reports so execution gaps are visible to the team.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.getTest().log(Status.SKIP, "Test skipped.");
        ExtentReportManager.unloadTest();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Runs after the current TestNG context finishes.
     * @param context : The TestNG execution context that just completed.
     * @return      : Nothing.
     * Why this way : This hook is present for future use if per-context cleanup or logging is added later.
     */
    @Override
    public void onFinish(ITestContext context) {
        // Report flush is handled once per suite in BaseTest @AfterSuite.
    }
}
