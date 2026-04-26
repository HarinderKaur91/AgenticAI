package com.agenticAI.autonomousFramework.bdd.hooks;

import com.agenticAI.autonomousFramework.Config.AppConfig;
import com.agenticAI.autonomousFramework.Enums.BrowserType;
import com.agenticAI.autonomousFramework.Utils.ExtentReportManager;
import com.agenticAI.autonomousFramework.Utils.LoggerUtil;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

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

    // Step-level reporting is handled by ExtentCucumberPlugin
    // (registered in CucumberTestNGRunner). The plugin captures actual
    // Gherkin step text — no @BeforeStep/@AfterStep hooks needed here.

    @After
    public void afterScenario(Scenario scenario) {
        try {
            // NOTE: Scenario pass/fail/skip status is reported per-step by
            // ExtentCucumberPlugin. Extent auto-rolls those step statuses
            // up to the scenario level, so we DO NOT add an extra
            // "Scenario PASSED/FAILED" log entry here (it would conflict
            // with the per-step results and look misleading).

            if (scenario.isFailed() && page != null && !page.isClosed()) {
                // Screenshot is also attached inline at the failed step by
                // ExtentCucumberPlugin. Here we only attach to the Cucumber
                // report so traces/videos line up.
                byte[] shot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                String screenshotName = "failure-" + scenarioSlug(scenario)
                        + "-" + System.currentTimeMillis() + ".png";
                scenario.attach(shot, "image/png", screenshotName);
            }
            if (context != null) {
                if (cfg.traceOnFailure() && scenario.isFailed()) {
                    Path tracePath = Paths.get("reports", "traces",
                            "bdd-" + scenarioSlug(scenario)
                                    + "-" + System.currentTimeMillis() + ".zip");
                    tracePath.getParent().toFile().mkdirs();
                    try { context.tracing().stop(new Tracing.StopOptions().setPath(tracePath)); }
                    catch (Exception ignored) {}
                } else if (cfg.traceOnFailure()) {
                    try { context.tracing().stop(); } catch (Exception ignored) {}
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

    private String scenarioSlug(Scenario scenario) {
        return scenario.getName().replaceAll("\\W+", "_");
    }
}
