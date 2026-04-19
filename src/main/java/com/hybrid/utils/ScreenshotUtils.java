package com.hybrid.utils;

import com.hybrid.constants.FrameworkConstants;
import com.hybrid.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Captures browser screenshots to disk and as Base64 text for reports.
 * Pattern Used: Utility pattern, because screenshot capture is a shared framework service.
 * Used By     : TestListener and any future debugging or reporting helper.
 * How to Extend: Add full-page screenshots, element screenshots, or video hooks if your project later needs richer evidence.
 */
public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Captures a screenshot file and saves it into the screenshots folder.
     * @param testName : Name of the test method requesting the screenshot.
     * @return      : Absolute file path of the saved PNG screenshot.
     * Why this way : Saving screenshots to disk gives the team permanent artifacts that can be reviewed after the run.
     */
    public static String captureScreenshot(String testName) {
        try {
            Files.createDirectories(Paths.get(FrameworkConstants.SCREENSHOTS_DIRECTORY));

            String timestamp = new SimpleDateFormat(FrameworkConstants.TIMESTAMP_PATTERN).format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            Path destinationPath = Paths.get(FrameworkConstants.SCREENSHOTS_DIRECTORY, fileName);

            TakesScreenshot screenshotDriver = (TakesScreenshot) DriverManager.getDriver();
            File sourceFile = screenshotDriver.getScreenshotAs(OutputType.FILE);

            Files.copy(sourceFile.toPath(), destinationPath);
            return destinationPath.toAbsolutePath().toString();
        } catch (IOException exception) {
            throw new RuntimeException("Unable to save screenshot for test: " + testName, exception);
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Captures a screenshot and returns it as a Base64-encoded String.
     * @param none   : This method does not accept parameters.
     * @return      : Base64 text representation of the screenshot image.
     * Why this way : ExtentReports can embed Base64 images directly into the HTML report without depending on external file links.
     */
    public static String getBase64Screenshot() {
        byte[] screenshotBytes = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
        return Base64.getEncoder().encodeToString(screenshotBytes);
    }
}
