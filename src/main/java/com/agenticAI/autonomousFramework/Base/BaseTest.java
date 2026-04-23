package com.agenticAI.autonomousFramework.Base;

import com.microsoft.playwright.*;
import com.agenticAI.autonomousFramework.Config.AppConfig;
import com.agenticAI.autonomousFramework.Enums.BrowserType;
import com.agenticAI.autonomousFramework.Pages.*;
import com.agenticAI.autonomousFramework.Utils.LoggerUtil;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected AppConfig cfg;

    protected HomePage homePage;
    protected LoginPage loginPage;
    protected ProductsPage productsPage;
    protected ProductDetailPage productDetailPage;
    protected CartPage cartPage;
    protected ContactUsPage contactUsPage;

    @BeforeClass
    public void setUpClass() {
        cfg = AppConfig.get();
        LoggerUtil.info("Initializing Playwright. env=" + cfg.environment()
                + " browser=" + cfg.browser() + " headless=" + cfg.headless()
                + " baseUrl=" + cfg.baseUrl());
        playwright = Playwright.create();

        com.microsoft.playwright.BrowserType browserType;
        BrowserType configured = cfg.browser();
        switch (configured) {
            case FIREFOX: browserType = playwright.firefox(); break;
            case WEBKIT:  browserType = playwright.webkit();  break;
            case CHROMIUM:
            default:      browserType = playwright.chromium();
        }

        browser = browserType.launch(new com.microsoft.playwright.BrowserType.LaunchOptions()
                .setHeadless(cfg.headless())
                .setSlowMo(cfg.slowMoMs()));
        LoggerUtil.info("Browser launched: " + configured);
    }

    @BeforeMethod
    public void setUpTest(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        LoggerUtil.startTestContext(testName, cfg.environment().name(), cfg.browser().name());
        LoggerUtil.info("Setting up test context and pages...");

        Browser.NewContextOptions ctxOpts = new Browser.NewContextOptions()
                .setViewportSize(cfg.viewportWidth(), cfg.viewportHeight());

        if (cfg.videoOnFailure()) {
            ctxOpts.setRecordVideoDir(Paths.get("reports", "videos"));
        }

        context = browser.newContext(ctxOpts);

        if (cfg.traceOnFailure()) {
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true)
                    .setTitle(testName));
        }

        page = context.newPage();
        page.setDefaultTimeout(cfg.defaultTimeoutMs());
        page.setDefaultNavigationTimeout(cfg.navigationTimeoutMs());

        homePage = new HomePage(page);
        loginPage = new LoginPage(page);
        productsPage = new ProductsPage(page);
        productDetailPage = new ProductDetailPage(page);
        cartPage = new CartPage(page);
        contactUsPage = new ContactUsPage(page);
        LoggerUtil.info("Test context setup complete");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTest(ITestResult result) {
        try {
            if (context != null) {
                if (cfg.traceOnFailure() && result.getStatus() == ITestResult.FAILURE) {
                    Path tracePath = Paths.get("reports", "traces",
                            result.getMethod().getMethodName() + "-" + System.currentTimeMillis() + ".zip");
                    tracePath.getParent().toFile().mkdirs();
                    try {
                        context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                        LoggerUtil.info("Playwright trace saved: " + tracePath.toAbsolutePath());
                    } catch (Exception e) {
                        LoggerUtil.warn("Failed to stop tracing: " + e.getMessage());
                    }
                } else if (cfg.traceOnFailure()) {
                    try { context.tracing().stop(); } catch (Exception ignored) {}
                }
                context.close();
            }
        } finally {
            LoggerUtil.info("Test context closed");
            LoggerUtil.clearTestContext();
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        LoggerUtil.info("Closing browser and Playwright...");
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        LoggerUtil.info("Browser and Playwright closed - Test suite complete");
    }
}
