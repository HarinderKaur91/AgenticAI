package com.Harinder.Playwright.Pages;
import com.microsoft.playwright.Page;

public class HomePage {

    private final Page page;
    private static final String BASE_URL = "https://automationexercise.com";

    public HomePage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate(BASE_URL);
    }

    public boolean isHomePageVisible() {
        page.locator("a[href='/products']").first().waitFor();
        return page.locator("a[href='/products']").first().isVisible();
    }

    public void clickProducts() {
        page.locator("a[href='/products']").first().click();
    }

    public void clickSignupLogin() {
        page.locator("a[href='/login']").first().click();
    }

    public void clickContactUs() {
        page.locator("a[href='/contact_us']").first().click();
    }

    public void clickTestCases() {
        page.locator("a[href='/test_cases']").first().click();
    }

    public void clickCart() {
        page.locator("a[href='/view_cart']").first().click();
    }

    public void subscribe(String email) {
        page.locator("#susbscribe_email").scrollIntoViewIfNeeded();
        page.locator("#susbscribe_email").fill(email);
        page.locator("#subscribe").click();
    }

    public String getSubscriptionSuccessMessage() {
        page.locator(".alert-success.alert").waitFor();
        return page.locator(".alert-success.alert").textContent().trim();
    }
}