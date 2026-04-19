package com.hybrid.constants;

import java.io.File;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Stores all framework-wide constant values in one centralized place.
 * Pattern Used: Constant Holder pattern, because centralizing hard-coded values improves maintainability.
 * Used By     : ConfigReader, ExcelUtils, ExtentReportManager, ScreenshotUtils, and BaseTest.
 * How to Extend: Add new paths, time formats, or default values here instead of scattering literals across classes.
 */
public final class FrameworkConstants {

    // Private constructor prevents accidental object creation for a utility-only constants class.
    private FrameworkConstants() {
    }

    // `user.dir` points to the Maven project root when the test is executed from that project.
    public static final String PROJECT_PATH = System.getProperty("user.dir");

    // Resource paths are built from the project root so they work on any machine cloning the project.
    public static final String CONFIG_FILE_PATH = PROJECT_PATH + File.separator
            + "src" + File.separator + "test" + File.separator + "resources" + File.separator
            + "config" + File.separator + "config.properties";

    public static final String TEST_DATA_FILE_PATH = PROJECT_PATH + File.separator
            + "src" + File.separator + "test" + File.separator + "resources" + File.separator
            + "testdata" + File.separator + "LoginTestData.xlsx";

    public static final String REPORTS_DIRECTORY = PROJECT_PATH + File.separator + "reports";
    public static final String SCREENSHOTS_DIRECTORY = PROJECT_PATH + File.separator + "screenshots";

    // Time stamp pattern is reused for report names and screenshot file names.
    public static final String TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss";

    // Default values keep the framework usable even if a config key is missing.
    public static final int DEFAULT_EXPLICIT_WAIT = 20;
    public static final int DEFAULT_POLLING_WAIT = 2;
    public static final int DEFAULT_PAGE_LOAD_TIMEOUT = 30;
    public static final String DEFAULT_BROWSER = "chrome";
}
