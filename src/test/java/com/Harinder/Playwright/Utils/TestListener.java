package com.Harinder.Playwright.Utils;

import com.microsoft.playwright.Page;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    private static final String SCREENSHOT_DIR = "screenshots";

    @Override
    public void onTestStart(ITestResult result) {
        LoggerUtil.info("========================================");
        LoggerUtil.info("Test Started: " + result.getMethod().getMethodName());
        LoggerUtil.info("========================================");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LoggerUtil.info("✓ Test Passed: " + result.getMethod().getMethodName());
        LoggerUtil.info("Duration: " + (result.getEndMillis() - result.getStartMillis()) + "ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LoggerUtil.error("✗ Test Failed: " + result.getMethod().getMethodName());
        LoggerUtil.error("Error: " + result.getThrowable().getMessage());
        
        // Take screenshot on failure
        Object testClass = result.getInstance();
        LoggerUtil.debug("Test instance class: " + (testClass != null ? testClass.getClass().getName() : "null"));
        
        if (testClass != null) {
            try {
                Page page = getPageFromTestClass(testClass);
                if (page != null) {
                    if (page.isClosed()) {
                        LoggerUtil.warn("Page is closed, cannot capture screenshot");
                    } else {
                        captureScreenshot(page, result.getMethod().getMethodName());
                    }
                } else {
                    LoggerUtil.warn("Page object not found in test class. Screenshots directory: " + new java.io.File(SCREENSHOT_DIR).getAbsolutePath());
                }
            } catch (Exception e) {
                LoggerUtil.error("Failed to capture screenshot: " + e.getMessage(), e);
            }
        } else {
            LoggerUtil.error("Test instance is null, cannot capture screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LoggerUtil.warn("⊘ Test Skipped: " + result.getMethod().getMethodName());
    }

    private Page getPageFromTestClass(Object testClass) {
        if (testClass == null) {
            LoggerUtil.debug("Test class is null");
            return null;
        }

        // Try to get the page field from the test class
        try {
            java.lang.reflect.Field pageField = testClass.getClass().getDeclaredField("page");
            pageField.setAccessible(true);
            Page page = (Page) pageField.get(testClass);
            LoggerUtil.debug("Successfully retrieved Page object from test class");
            return page;
        } catch (NoSuchFieldException e) {
            LoggerUtil.debug("Page field not found in direct class, trying inherited classes");
            
            // Try parent classes
            Class<?> parentClass = testClass.getClass().getSuperclass();
            while (parentClass != null) {
                try {
                    java.lang.reflect.Field pageField = parentClass.getDeclaredField("page");
                    pageField.setAccessible(true);
                    Page page = (Page) pageField.get(testClass);
                    LoggerUtil.debug("Successfully retrieved Page object from parent class: " + parentClass.getName());
                    return page;
                } catch (NoSuchFieldException ex) {
                    parentClass = parentClass.getSuperclass();
                } catch (IllegalAccessException ex) {
                    LoggerUtil.error("Cannot access Page field in parent class: " + ex.getMessage());
                    parentClass = parentClass.getSuperclass();
                }
            }
            LoggerUtil.warn("Page field not found in test class or any parent class");
        } catch (IllegalAccessException e) {
            LoggerUtil.error("Cannot access Page field: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("Unexpected error getting Page field: " + e.getMessage());
        }
        
        return null;
    }

    private void captureScreenshot(Page page, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotPath);
            LoggerUtil.debug("Screenshots directory ensured: " + screenshotPath.toAbsolutePath());

            // Create timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_FAILED_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + "/" + fileName;

            // Capture screenshot
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
            LoggerUtil.info("✓ Screenshot successfully saved: " + filePath);
            LoggerUtil.info("  Absolute path: " + Paths.get(filePath).toAbsolutePath());
        } catch (IOException e) {
            LoggerUtil.error("IOException - Failed to save screenshot: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("Unexpected error capturing screenshot: " + e.getMessage(), e);
        }
    }
}
