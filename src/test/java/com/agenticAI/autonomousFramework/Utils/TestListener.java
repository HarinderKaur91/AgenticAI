package com.agenticAI.autonomousFramework.Utils;

import com.agenticAI.autonomousFramework.Annotations.JiraTestMeta;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class TestListener implements ITestListener {

    private static final String SCREENSHOT_DIR = "screenshots";

    @Override
    public void onTestStart(ITestResult result) {
        LoggerUtil.info("========================================");
        LoggerUtil.info("Test Started: " + result.getMethod().getMethodName()
                + " [correlationId=" + LoggerUtil.correlationId() + "]");
        LoggerUtil.info("========================================");

        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription() != null
                ? result.getMethod().getDescription() : "";

        JiraTestMeta meta = readMeta(result);
        ExtentReportManager.createTest(testName, description, meta);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LoggerUtil.info("PASS: " + result.getMethod().getMethodName()
                + " (" + (result.getEndMillis() - result.getStartMillis()) + "ms)");

        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.PASS, "Test passed in " + (result.getEndMillis() - result.getStartMillis()) + "ms");
        }
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LoggerUtil.error("FAIL: " + result.getMethod().getMethodName()
                + " - " + result.getThrowable().getMessage());

        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
            test.fail(result.getThrowable());
        }

        Object testInstance = result.getInstance();
        if (testInstance != null) {
            try {
                Page page = getPageFromTestClass(testInstance);
                if (page != null && !page.isClosed()) {
                    captureScreenshot(page, result.getMethod().getMethodName());
                    attachVideoIfPresent(page, result.getMethod().getMethodName());
                } else {
                    LoggerUtil.warn("Page not available for screenshot capture");
                }
            } catch (Exception e) {
                LoggerUtil.error("Failed to capture failure artifacts: " + e.getMessage(), e);
            }
        }
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LoggerUtil.warn("SKIP: " + result.getMethod().getMethodName());

        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, "Test skipped: " +
                    (result.getThrowable() != null ? result.getThrowable().getMessage() : "N/A"));
        }
        ExtentReportManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flushReports();
        LoggerUtil.info("Extent Report generated in reports/ directory");
    }

    private JiraTestMeta readMeta(ITestResult result) {
        try {
            Method method = result.getMethod().getConstructorOrMethod().getMethod();
            JiraTestMeta meta = method.getAnnotation(JiraTestMeta.class);
            if (meta == null) {
                meta = method.getDeclaringClass().getAnnotation(JiraTestMeta.class);
            }
            return meta;
        } catch (Exception e) {
            return null;
        }
    }

    private Page getPageFromTestClass(Object testClass) {
        if (testClass == null) return null;
        Class<?> current = testClass.getClass();
        while (current != null && current != Object.class) {
            try {
                java.lang.reflect.Field pageField = current.getDeclaredField("page");
                pageField.setAccessible(true);
                Object value = pageField.get(testClass);
                if (value instanceof Page) {
                    return (Page) value;
                }
            } catch (NoSuchFieldException ignored) {
                // try parent
            } catch (IllegalAccessException ex) {
                LoggerUtil.error("Cannot access Page field: " + ex.getMessage());
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private void captureScreenshot(Page page, String testName) {
        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotDir);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_FAILED_" + timestamp + ".png";
            Path filePath = screenshotDir.resolve(fileName);

            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(filePath)
                    .setFullPage(true));
            LoggerUtil.info("Screenshot saved: " + filePath.toAbsolutePath());

            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);
                test.fail("Screenshot on failure",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
            }
        } catch (IOException e) {
            LoggerUtil.error("Failed to save screenshot: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("Unexpected screenshot error: " + e.getMessage(), e);
        }
    }

    private void attachVideoIfPresent(Page page, String testName) {
        try {
            if (page.video() != null) {
                Path videoPath = page.video().path();
                if (videoPath != null) {
                    LoggerUtil.info("Playwright video saved: " + videoPath.toAbsolutePath());
                    ExtentTest test = ExtentReportManager.getTest();
                    if (test != null) {
                        test.info("Video: <a href='file://" + videoPath.toAbsolutePath()
                                + "' target='_blank'>" + videoPath.getFileName() + "</a>");
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtil.debug("No video to attach: " + e.getMessage());
        }
    }
}

