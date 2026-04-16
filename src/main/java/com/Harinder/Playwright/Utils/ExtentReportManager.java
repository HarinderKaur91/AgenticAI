package com.Harinder.Playwright.Utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    private static void createInstance() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String reportPath = "reports/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Playwright Test Report");
        sparkReporter.config().setReportName("AutomationExercise Test Results");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Browser", System.getProperty("browser", "chromium"));
        extent.setSystemInfo("Headless", System.getProperty("headless", "true"));
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTest.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void removeTest() {
        extentTest.remove();
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}
