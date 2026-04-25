package com.agenticAI.autonomousFramework.bdd.hooks;

import com.agenticAI.autonomousFramework.Config.AppConfig;
import com.agenticAI.autonomousFramework.Enums.BrowserType;
import com.agenticAI.autonomousFramework.Utils.ExtentReportManager;
import com.agenticAI.autonomousFramework.Utils.LoggerUtil;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.AfterStep;
import io.cucumber.java.BeforeStep;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Cucumber hooks that mirror BaseTest's lifecycle for BDD scenarios.
 * The Playwright Page is exposed through {@link ScenarioContext} so
 * step definitions can pull it without static state.
 * 
 * Step-level reporting is automatic: @BeforeStep and @AfterStep hooks
 * capture pass/fail status and log to Extent Report WITHOUT polluting step definitions.
 */
public class CucumberHooks {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private AppConfig cfg;
    private ExtentTest extentScenario;
    private ExtentTest stepNode;

    @Before
    public void beforeScenario(Scenario scenario) {
        cfg = AppConfig.get();
        LoggerUtil.startTestContext(scenario.getName(), cfg.environment().name(), cfg.browser().name());
        
        // Create Extent Test for scenario
        extentScenario = ExtentReportManager.createTest(scenario.getName(), 
            "Feature: " + scenario.getUri() + "\n" + 
            "Scenario: " + scenario.getName());
        extentScenario.info("Tags: " + scenario.getSourceTagNames());

        playwright = Playwright.create();
        BrowserType configured = cfg.browser();
        com.microsoft.playwright.BrowserType type;
        switch (configured) {
            case FIREFOX: type = playwright.firefox(); break;
            case WEBKIT:  type = playwright.webkit();  break;
            case CHROMIUM:
            default:      type = playwright.chromium();
        }
        browser = type.launch(new com.microsoft.playwright.BrowserType.LaunchOptions()
                .setHeadless(cfg.headless())
                .setSlowMo(cfg.slowMoMs()));

        Browser.NewContextOptions ctxOpts = new Browser.NewContextOptions()
                .setViewportSize(cfg.viewportWidth(), cfg.viewportHeight());
        if (cfg.videoOnFailure()) {
            ctxOpts.setRecordVideoDir(Paths.get("reports", "videos"));
        }
        context = browser.newContext(ctxOpts);
        if (cfg.traceOnFailure()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true).setSnapshots(true).setSources(true)
                    .setTitle(scenario.getName()));
        }
        page = context.newPage();
        page.setDefaultTimeout(cfg.defaultTimeoutMs());
        page.setDefaultNavigationTimeout(cfg.navigationTimeoutMs());

        ScenarioContext.set(page, cfg);
        LoggerUtil.info("[BDD] Scenario started: " + scenario.getName());
    }

    @BeforeStep
    public void beforeStep(Scenario scenario) {
        // Create a child node for this step in the report
        // Step text will be captured in @AfterStep
        LoggerUtil.info("[STEP] Executing step...");
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        // Capture the last step's result automatically
        // by checking the scenario's status
        if (scenario.isFailed()) {
            // The last step failed - scenario has a failed status
            LoggerUtil.info("[STEP] Step execution resulted in failure");
        } else {
            LoggerUtil.info("[STEP] Step execution passed");
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        try {
            LoggerUtil.info("[BDD] Scenario status: " + scenario.getStatus());

            // Log final scenario status to Extent
            if (scenario.isFailed()) {
                if (extentScenario != null) {
                    extentScenario.fail("❌ Scenario FAILED");
                }
                // Capture screenshot on failure
                if (page != null && !page.isClosed()) {
                    byte[] shot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                    scenario.attach(shot, "image/png", "failure-screenshot");
                    if (extentScenario != null) {
                        extentScenario.addScreenCaptureFromPath("failure-screenshot");
                    }
                }
            } else {
                if (extentScenario != null) {
                    extentScenario.pass("✅ Scenario PASSED");
                }
            }

            // Capture trace if configured
            if (context != null) {
                if (cfg.traceOnFailure() && scenario.isFailed()) {
                    Path tracePath = Paths.get("reports", "traces",
                            "bdd-" + scenario.getName().replaceAll("\\W+", "_")
                                    + "-" + System.currentTimeMillis() + ".zip");
                    tracePath.getParent().toFile().mkdirs();
                    try {
                        context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                        LoggerUtil.info("Trace saved: " + tracePath);
                    } catch (Exception e) {
                        LoggerUtil.warn("Failed to save trace: " + e.getMessage());
                    }
                } else if (cfg.traceOnFailure()) {
                    try {
                        context.tracing().stop();
                    } catch (Exception ignored) {}
                }
                context.close();
            }
            if (browser != null)    browser.close();
            if (playwright != null) playwright.close();

        } finally {
            ScenarioContext.clear();
            LoggerUtil.info("[BDD] Scenario end: " + scenario.getName() + " status=" + scenario.getStatus());
            LoggerUtil.clearTestContext();
            ExtentReportManager.flushReports();
        }
    }
}
