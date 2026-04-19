package com.hybrid.utils;

import com.hybrid.constants.FrameworkConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Loads and exposes values from config.properties for the whole framework.
 * Pattern Used: Singleton-style static utility, because configuration should be loaded once and reused everywhere.
 * Used By     : BaseTest, BrowserFactory, LoginTest, and reporting utilities.
 * How to Extend: Add new keys into config.properties and expose typed getter methods here when needed.
 */
public final class ConfigReader {

    // Single Properties object stores key-value pairs loaded from config.properties.
    private static final Properties PROPERTIES = new Properties();

    // Static block executes once when the class is first loaded into memory.
    static {
        loadProperties();
    }

    private ConfigReader() {
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Loads the framework configuration file into memory.
     * @param none   : This method does not accept parameters.
     * @return      : Nothing. It initializes the static Properties object.
     * Why this way : We load the file once during class initialization so every test can read config values
     *                quickly without reopening the file repeatedly.
     */
    private static void loadProperties() {
        try (InputStream inputStream = new FileInputStream(FrameworkConstants.CONFIG_FILE_PATH)) {
            PROPERTIES.load(inputStream);
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load config.properties from: "
                    + FrameworkConstants.CONFIG_FILE_PATH, exception);
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the value of a config key as a String.
     * @param key    : The property name to read from config.properties.
     * @return      : The configured value if found, otherwise an empty String.
     * Why this way : Returning an empty String instead of null helps avoid NullPointerException
     *                in downstream code and keeps beginner debugging simpler.
     */
    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key, "").trim();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Returns the value of a config key or a supplied default.
     * @param key    : The property name to read from config.properties.
     * @param defaultValue : The fallback value to use when the key does not exist.
     * @return      : The configured value if present, otherwise the provided default value.
     * Why this way : Defaults make the framework resilient when a junior engineer adds new keys gradually.
     */
    public static String getProperty(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue).trim();
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Reads a config key and converts it into an integer.
     * @param key    : The property name whose numeric value is needed.
     * @return      : Integer value from the config file, or framework default if missing/invalid.
     * Why this way : Parsing is centralized here so test classes do not repeat conversion logic.
     */
    public static int getIntProperty(String key) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (NumberFormatException exception) {
            return FrameworkConstants.DEFAULT_EXPLICIT_WAIT;
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Reads a config key and converts it into a boolean.
     * @param key    : The property name whose true/false value is needed.
     * @return      : Boolean interpretation of the configured text.
     * Why this way : `Boolean.parseBoolean` is safe because it never throws an exception;
     *                non-true values simply resolve to false.
     */
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
}
