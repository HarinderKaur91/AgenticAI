package com.agenticAI.autonomousFramework.Utils;

import com.agenticAI.autonomousFramework.Annotations.JiraTestMeta;
import com.agenticAI.autonomousFramework.Config.AppConfig;
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
        sparkReporter.config().setDocumentTitle("AgenticAI Autonomous Framework Report");
        sparkReporter.config().setReportName("AutomationExercise Test Results");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        AppConfig cfg = AppConfig.get();
        extent.setSystemInfo("Environment",      cfg.environment().name());
        extent.setSystemInfo("Base URL",         cfg.baseUrl());
        extent.setSystemInfo("Browser",          cfg.browser().name());
        extent.setSystemInfo("Headless",         String.valueOf(cfg.headless()));
        extent.setSystemInfo("OS",               System.getProperty("os.name"));
        extent.setSystemInfo("Java Version",     System.getProperty("java.version"));
        extent.setSystemInfo("User",             System.getProperty("user.name"));
        String gitSha = System.getenv().getOrDefault("GIT_COMMIT",
                System.getenv().getOrDefault("GITHUB_SHA", "local"));
        extent.setSystemInfo("Git Commit",       gitSha);
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTest.set(test);
        return test;
    }

    /**
     * Creates a test node and attaches Jira/Zephyr badges from the
     * {@link JiraTestMeta} annotation when present.
     */
    public static ExtentTest createTest(String testName, String description, JiraTestMeta meta) {
        ExtentTest test = createTest(testName, description);
        if (meta != null) {
            AppConfig cfg = AppConfig.get();
            if (!meta.jira().isEmpty()) {
                String url = cfg.jiraBaseUrl() + "/browse/" + meta.jira();
                test.assignCategory("JIRA: " + meta.jira());
                test.info("Jira: <a href='" + url + "' target='_blank'>" + meta.jira() + "</a>");
            }
            if (!meta.zephyr().isEmpty()) {
                test.assignCategory("ZEPHYR: " + meta.zephyr());
                test.info("Zephyr: " + meta.zephyr());
            }
            if (!meta.story().isEmpty()) {
                test.info("Story: " + meta.story());
            }
            test.assignCategory("Severity: " + meta.severity().name());
        }
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

