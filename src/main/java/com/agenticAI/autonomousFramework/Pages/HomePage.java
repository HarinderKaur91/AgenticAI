package com.agenticAI.autonomousFramework.Pages;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import com.agenticAI.autonomousFramework.Config.AppConfig;
import com.agenticAI.autonomousFramework.Enums.NavigationTarget;
import com.agenticAI.autonomousFramework.Utils.LoggerUtil;

public class HomePage {

    private final Page page;
    private final String baseUrl;

    public HomePage(Page page) {
        this.page = page;
        this.baseUrl = AppConfig.get().baseUrl();
    }

    public void open() {
        LoggerUtil.info("Navigating to: " + baseUrl);
        page.navigate(baseUrl);
        LoggerUtil.info("Home page opened successfully");
    }

    public boolean isHomePageVisible() {
        LoggerUtil.debug("Checking if home page is visible");
        page.locator("a[href='/products']").first().waitFor();
        return page.locator("a[href='/products']").first().isVisible();
    }

    public void clickProducts() {
        LoggerUtil.info("Clicking on Products link");
        Locator productsLink = page.locator("a[href='/products']").first();
        productsLink.waitFor();
        try {
            productsLink.click();
            page.waitForURL("**" + NavigationTarget.PRODUCTS.path(),
                    new Page.WaitForURLOptions().setTimeout(10000));
        } catch (PlaywrightException ex) {
            LoggerUtil.warn("Products link click did not navigate; falling back to direct navigation.");
            page.navigate(baseUrl + NavigationTarget.PRODUCTS.path());
            page.waitForURL("**" + NavigationTarget.PRODUCTS.path());
        }
    }

    public void clickSignupLogin() {
        LoggerUtil.info("Clicking on Signup/Login link");
        page.locator("a[href='/login']").first().click();
    }

    public void clickContactUs() {
        LoggerUtil.info("Clicking on Contact Us link");
        page.locator("a[href='/contact_us']").first().click();
    }

    public void clickTestCases() {
        LoggerUtil.info("Navigating to Test Cases page");
        page.navigate(baseUrl + NavigationTarget.TEST_CASES.path());
    }

    public void clickCart() {
        LoggerUtil.info("Clicking on Cart link");
        page.locator("a[href='/view_cart']").first().click();
    }

    public void subscribe(String email) {
        LoggerUtil.info("Subscribing with email: " + email);
        page.locator("#susbscribe_email").scrollIntoViewIfNeeded();
        page.locator("#susbscribe_email").fill(email);
        page.locator("#subscribe").click();
        LoggerUtil.info("Subscription completed");
    }

    public String getSubscriptionSuccessMessage() {
        page.locator(".alert-success.alert").waitFor();
        String message = page.locator(".alert-success.alert").textContent().trim();
        LoggerUtil.info("Subscription message: " + message);
        return message;
    }

    public boolean isSubscriptionSuccessMessageVisible() {
        Locator successMessage = page.locator(".alert-success.alert");
        return successMessage.count() > 0 && successMessage.first().isVisible();
    }
}
