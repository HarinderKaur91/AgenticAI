package com.Harinder.Playwright.Pages;

import com.microsoft.playwright.Page;

public class LoginPage {

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public boolean isLoginPageVisible() {
        page.locator("text=Login to your account").waitFor();
        return page.locator("text=Login to your account").isVisible();
    }

    public void signup(String name, String email) {
        page.locator("input[data-qa='signup-name']").fill(name);
        page.locator("input[data-qa='signup-email']").fill(email);
        page.locator("button[data-qa='signup-button']").click();
    }

    public boolean isEnterAccountInfoVisible() {
        page.locator("text=Enter Account Information").waitFor();
        return page.locator("text=Enter Account Information").isVisible();
    }

    public void fillAccountInformation(String password) {
        page.locator("#id_gender1").check();
        page.locator("input[data-qa='password']").fill(password);
        page.locator("select[data-qa='days']").selectOption("10");
        page.locator("select[data-qa='months']").selectOption("5");
        page.locator("select[data-qa='years']").selectOption("1995");

        page.locator("input[data-qa='first_name']").fill("Harinder");
        page.locator("input[data-qa='last_name']").fill("Kaur");
        page.locator("input[data-qa='company']").fill("QA Company");
        page.locator("input[data-qa='address']").fill("123 Main Street");
        page.locator("input[data-qa='address2']").fill("Suite 10");
        page.locator("select[data-qa='country']").selectOption("Canada");
        page.locator("input[data-qa='state']").fill("Alberta");
        page.locator("input[data-qa='city']").fill("Calgary");
        page.locator("input[data-qa='zipcode']").fill("T2X1V4");
        page.locator("input[data-qa='mobile_number']").fill("1234567890");
    }

    public void clickCreateAccount() {
        page.locator("button[data-qa='create-account']").click();
    }

    public String getAccountCreatedMessage() {
        page.locator("h2[data-qa='account-created']").waitFor();
        return page.locator("h2[data-qa='account-created']").innerText().trim();
    }

    public void clickContinue() {
        page.locator("a[data-qa='continue-button']").click();
    }

    public boolean isLoggedInAsVisible(String name) {
        page.locator("text=Logged in as").waitFor();
        return page.locator("text=Logged in as " + name).isVisible();
    }

    public void clickLogout() {
        page.locator("a[href='/logout']").click();
    }

    public void login(String email, String password) {
        page.locator("input[data-qa='login-email']").fill(email);
        page.locator("input[data-qa='login-password']").fill(password);
        page.locator("button[data-qa='login-button']").click();
    }

    public void clickDeleteAccount() {
        page.locator("a[href='/delete_account']").click();
    }

    public String getAccountDeletedMessage() {
        page.locator("h2[data-qa='account-deleted']").waitFor();
        return page.locator("h2[data-qa='account-deleted']").innerText().trim();
    }
}