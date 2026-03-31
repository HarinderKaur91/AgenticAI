# Logging & Screenshots Setup Guide

## Files Added/Modified

1. **pom.xml** - Added SLF4J and Log4j2 dependencies
2. **log4j2.xml** - Log4j2 configuration file
3. **LoggerUtil.java** - Logging utility class
4. **TestListener.java** - Test listener for automatic screenshots on failure
5. **testng.xml** - Registered TestListener
6. **BaseTest.java** - Added logging to setup/teardown
7. **HomePage.java** - Example of adding logs to page classes

## How to Run Tests

```bash
# Run tests normally (logs and screenshots enabled)
mvn clean test

# Run in headless mode with logs
mvn clean test -Dheadless=true

# Run with Firefox browser
mvn clean test -Dbrowser=firefox
```

## Where to Find Output

### Logs
```
logs/test-execution.log          # Main log file
logs/test-execution-*.log        # Rolled over logs (max 10 files, 10MB each)
```

### Screenshots
```
screenshots/                     # Folder with all failure screenshots
screenshots/testName_timestamp.png
```

### Test Reports
```
target/surefire-reports/emailable-report.html  # HTML test report
target/surefire-reports/TEST-*.xml              # XML results
```

## Log Levels

- **INFO** - Important test events (navigation, clicks, form submission)
- **DEBUG** - Detailed diagnostic information
- **WARN** - Warning messages
- **ERROR** - Error messages with stack traces
- **FATAL** - Critical failures

## Using LoggerUtil in Your Code

Add logging to any page class or test:

```java
import com.Harinder.Playwright.Utils.LoggerUtil;

// In your methods:
LoggerUtil.info("Navigating to home page");
LoggerUtil.debug("Element found and visible");
LoggerUtil.warn("Slow response time");
LoggerUtil.error("Element not found: " + exception);
```

## Console Output Example

```
2024-01-15 10:30:45.123 [main] INFO  com.Harinder.Playwright.Utils.LoggerUtil - Initializing Playwright and Browser...
2024-01-15 10:30:45.234 [main] INFO  com.Harinder.Playwright.Utils.LoggerUtil - Browser: chromium | Headless: false
2024-01-15 10:30:47.456 [main] INFO  com.Harinder.Playwright.Utils.LoggerUtil - ========================================
2024-01-15 10:30:47.567 [main] INFO  com.Harinder.Playwright.Utils.LoggerUtil - Test Started: verifyHomePageLoads
2024-01-15 10:30:47.678 [main] INFO  com.Harinder.Playwright.Utils.LoggerUtil - Navigating to: https://automationexercise.com
```

## Screenshot Capture

Screenshots are automatically captured for failed tests:
- File naming: `testMethodName_yyyyMMdd_HHmmss.png`
- Located in `screenshots/` folder
- Useful for debugging test failures

## Tips

1. Always use `LoggerUtil` instead of `System.out.println()`
2. Add logs at key points: navigation, interactions, assertions
3. Check both console output and `logs/test-execution.log` for detailed analysis
4. Screenshots help identify UI issues at the moment of failure
5. Review HTML report after running tests for summary view
