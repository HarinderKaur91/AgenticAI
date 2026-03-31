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
        if (testClass != null) {
            try {
                Page page = getPageFromTestClass(testClass);
                if (page != null && !page.isClosed()) {
                    captureScreenshot(page, result.getMethod().getMethodName());
                }
            } catch (Exception e) {
                LoggerUtil.error("Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LoggerUtil.warn("⊘ Test Skipped: " + result.getMethod().getMethodName());
    }

    private Page getPageFromTestClass(Object testClass) {
        try {
            java.lang.reflect.Field pageField = testClass.getClass().getDeclaredField("page");
            pageField.setAccessible(true);
            return (Page) pageField.get(testClass);
        } catch (Exception e) {
            return null;
        }
    }

    private void captureScreenshot(Page page, String testName) {
        try {
            // Create screenshots directory if it doesn't exist
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotPath);

            // Create timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + "/" + fileName;

            // Capture screenshot
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
            LoggerUtil.info("Screenshot saved: " + filePath);
        } catch (IOException e) {
            LoggerUtil.error("Failed to save screenshot: " + e.getMessage());
        }
    }
}
