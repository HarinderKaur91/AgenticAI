package com.Harinder.Playwright.Base;

import com.microsoft.playwright.*;
import com.Harinder.Playwright.Pages.*;
import com.Harinder.Playwright.Utils.LoggerUtil;
import org.testng.annotations.*;

public class BaseTest {
    
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected HomePage homePage;
    protected LoginPage loginPage;
    protected ProductsPage productsPage;
    protected ProductDetailPage productDetailPage;
    protected CartPage cartPage;
    protected ContactUsPage contactUsPage;

    @BeforeClass
    public void setUpClass() {  
        LoggerUtil.info("Initializing Playwright and Browser...");
        playwright = Playwright.create();

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", System.getenv("CI") != null ? "true" : "false"));
        String browserName = System.getProperty("browser", "chromium");
        LoggerUtil.info("Browser: " + browserName + " | Headless: " + headless);

        BrowserType browserType;
        switch (browserName.toLowerCase()) {
            case "firefox":
                browserType = playwright.firefox();
                break;
            case "webkit":
                browserType = playwright.webkit();
                break;
            default:
                browserType = playwright.chromium();
        }

        browser = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(100)
                .setArgs(java.util.Arrays.asList(
                        "--blink-settings=imagesEnabled=false",
                        "--disable-extensions"
                )));
        LoggerUtil.info("Browser launched successfully");
    }

    @BeforeMethod
    public void setUpTest() {
        LoggerUtil.info("Setting up test context and pages...");
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 900));
        page = context.newPage();
        // Increase page timeout to handle slow ad iframes
        page.setDefaultTimeout(60000);
        page.setDefaultNavigationTimeout(60000);

        homePage = new HomePage(page);
        loginPage = new LoginPage(page);
        productsPage = new ProductsPage(page);
        productDetailPage = new ProductDetailPage(page);
        cartPage = new CartPage(page);
        contactUsPage = new ContactUsPage(page);
        LoggerUtil.info("Test context setup complete");
    }

    @AfterMethod
    public void tearDownTest() {
        LoggerUtil.info("Tearing down test context...");
        if (context != null) {
            context.close();
        }
        LoggerUtil.info("Test context closed");
    }

    @AfterClass
    public void tearDownClass() {
        LoggerUtil.info("Closing browser and Playwright...");
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        LoggerUtil.info("Browser and Playwright closed - Test suite complete");
    }
}