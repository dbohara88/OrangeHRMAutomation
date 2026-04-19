# HybridQAFramework

HybridQAFramework is a production-ready Selenium automation framework built with Java, TestNG, and Maven. It follows a hybrid approach using Page Object Model, data-driven testing with Excel, cross-browser support, parallel execution, and ExtentReports-based reporting.

The project was designed to be beginner-friendly, so the framework classes include detailed comments explaining what each component does, why it exists, and how to extend it safely.

## Tech Stack

- Java 11+
- Selenium WebDriver 4
- TestNG 7
- Maven
- WebDriverManager
- Apache POI
- ExtentReports 5
- Log4j2

## Project Structure

```text
HybridQAFramework/
├── pom.xml
├── testng.xml
├── testng-regression.xml
├── src/
│   ├── main/java/com/hybrid/
│   │   ├── base/
│   │   ├── constants/
│   │   ├── driver/
│   │   ├── listeners/
│   │   ├── pages/
│   │   ├── reports/
│   │   └── utils/
│   └── test/
│       ├── java/com/hybrid/tests/
│       └── resources/
│           ├── config/
│           └── testdata/
├── reports/
└── screenshots/
```

## Features

- Thread-safe WebDriver management with `ThreadLocal`
- Page Object Model with reusable base page methods
- Cross-browser execution for Chrome, Firefox, and Edge
- Parallel execution through TestNG suite configuration
- Excel-driven test data using Apache POI
- ExtentReports HTML reporting
- Automatic screenshot capture on failure
- Config-driven environment and browser settings

## Prerequisites

- Java 11 or higher
- Maven 3.8 or higher
- Chrome, Firefox, or Edge installed
- IntelliJ IDEA, Eclipse, or VS Code with Java support

## How To Run

Run the full suite:

```bash
mvn clean test
```

Run the regression suite only:

```bash
mvn test "-Dsurefire.suiteXmlFiles=testng-regression.xml"
```

Run on a specific browser:

```bash
mvn clean test "-Dbrowser=firefox"
```

Run in headed mode for debugging:

```bash
mvn clean test "-Dheadless=false"
```

## Configuration

Framework settings live in:

- `src/test/resources/config/config.properties`

Typical values include:

- `base.url`
- `browser`
- `headless`
- `explicit.wait`
- `page.load.timeout`
- valid application credentials

## Reports And Screenshots

After execution:

- HTML reports are generated in `reports/`
- Failure screenshots are saved in `screenshots/`
- Surefire test results are available in `target/surefire-reports/`

## How To Add A New Page Object

1. Create a new class inside `src/main/java/com/hybrid/pages/`
2. Extend `BasePage`
3. Add page locators using `@FindBy`
4. Initialize the page with `super(driver)`
5. Add reusable business methods for that page
6. Use the page object from a test class instead of writing raw Selenium code in tests

## How To Add More Excel Data

1. Open `src/test/resources/testdata/LoginTestData.xlsx`
2. Go to the `Login` sheet
3. Add new rows below the existing header
4. Save the workbook
5. Re-run the suite

The `@DataProvider` in `LoginTest` will automatically read all rows through `ExcelUtils`.

## CI/CD Note

For Jenkins freestyle jobs, a simple build step can be:

```bash
mvn clean test "-Dbrowser=chrome"
```

## Notes

- The framework is reusable across applications because the base URL and credentials come from `config.properties`
- Page objects and assertions can be adapted to any target application
- The included sample tests demonstrate login and landing-page verification patterns
