package com.Harinder.Playwright.Pages;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.Harinder.Playwright.Utils.LoggerUtil;

public class HomePage {

    private final Page page;
    private static final String BASE_URL = "https://automationexercise.com";

    public HomePage(Page page) {
        this.page = page;
    }

    public void open() {
        LoggerUtil.info("Navigating to: " + BASE_URL);
        page.navigate(BASE_URL);
        LoggerUtil.info("Home page opened successfully");
    }

    public boolean isHomePageVisible() {
        LoggerUtil.debug("Checking if home page is visible");
        page.locator("a[href='/products']").first().waitFor();
        return page.locator("a[href='/products']").first().isVisible();
    }

    public void clickProducts() {
        LoggerUtil.info("Clicking on Products link");
        page.locator("a[href='/products']").first().click();
        page.waitForURL("**/products");
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
        page.navigate(BASE_URL + "/test_cases");
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
