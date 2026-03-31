package com.Harinder.Playwright.Base;

import com.microsoft.playwright.*;
import com.Harinder.Playwright.Pages.*;
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
        playwright = Playwright.create();

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        String browserName = System.getProperty("browser", "chromium");

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
                .setSlowMo(100));
    }

    @BeforeMethod
    public void setUpTest() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 900));
        page = context.newPage();

        homePage = new HomePage(page);
        loginPage = new LoginPage(page);
        productsPage = new ProductsPage(page);
        productDetailPage = new ProductDetailPage(page);
        cartPage = new CartPage(page);
        contactUsPage = new ContactUsPage(page);
    }

    @AfterMethod
    public void tearDownTest() {
        if (context != null) {
            context.close();
        }
    }

    @AfterClass
    public void tearDownClass() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}