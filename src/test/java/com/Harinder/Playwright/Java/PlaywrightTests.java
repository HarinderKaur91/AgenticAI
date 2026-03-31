package com.Harinder.Playwright.Java;
import com.Harinder.Playwright.Base.BaseTest;
import com.Harinder.Playwright.Utils.TestDataUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;
import java.util.List;

public class PlaywrightTests extends BaseTest {

    @Test(priority = 1)
    public void verifyHomePageLoads() {
        homePage.open();
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page did not load properly.");
    }

    @Test(priority = 2)
    public void verifyTestCasesPageNavigation() {
        homePage.open();
        homePage.clickTestCases();
        Assert.assertTrue(page.url().contains("/test_cases"), "User is not on test cases page.");
    }

    @Test(priority = 3)
    public void verifyProductsPageIsVisible() {
        homePage.open();
        homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page is not visible.");
        Assert.assertTrue(productsPage.getVisibleProductCount() > 0, "No products are displayed.");
    }

    @Test(priority = 4)
    public void verifySearchProduct() {
        homePage.open();
        homePage.clickProducts();

        productsPage.searchProduct("Blue Top");

        Assert.assertTrue(productsPage.isSearchedProductsVisible(), "Searched Products heading not visible.");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(), "Search results are not displayed.");
    }

    @Test(priority = 5)
    public void verifyFirstProductDetailPage() {
        homePage.open();
        homePage.clickProducts();

        productsPage.openProductDetailByIndex(0);

        Assert.assertTrue(productDetailPage.isProductDetailVisible(), "Product detail page is not visible.");
        Assert.assertFalse(productDetailPage.getProductName().isEmpty(), "Product name is empty.");
    }

    @Test(priority = 6)
    public void verifyAddSingleProductToCart() {
        homePage.open();
        homePage.clickProducts();

        productsPage.openProductDetailByIndex(0);
        String productName = productDetailPage.getProductName();

        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");
        List<String> names = cartPage.getCartProductNames();
        Assert.assertTrue(names.contains(productName), "Expected product not found in cart.");
    }

    @Test(priority = 7)
    public void verifyProductQuantityInCart() {
        homePage.open();
        homePage.clickProducts();

        productsPage.openProductDetailByIndex(0);

        productDetailPage.setQuantity("4");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");
        Assert.assertEquals(cartPage.getQuantityByRow(0), "4", "Incorrect product quantity in cart.");
    }

    @Test(priority = 8)
    public void verifySubscriptionOnHomePage() {
        homePage.open();
        homePage.subscribe(TestDataUtil.uniqueEmail());

        String successMessage = homePage.getSubscriptionSuccessMessage();
        Assert.assertTrue(successMessage.contains("successfully subscribed"),
                "Subscription success message is not displayed.");
    }

    @Test(priority = 9)
    public void verifyContactUsFormSubmission() {
        homePage.open();
        homePage.clickContactUs();

        Assert.assertTrue(contactUsPage.isContactUsPageVisible(), "Contact Us page is not visible.");

        String filePath = Paths.get("src/test/resources/test-upload.txt").toAbsolutePath().toString();

        contactUsPage.submitContactForm(
                "Harinder",
                TestDataUtil.uniqueEmail(),
                "Playwright Java Test",
                "This is a sample message from Playwright automation.",
                filePath
        );

        String successMessage = contactUsPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("Success! Your details have been submitted successfully."),
                "Contact form success message is not displayed.");
    }

    @Test(priority = 10)
    public void verifyRegisterLoginLogoutAndDeleteUser() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        homePage.open();
        homePage.clickSignupLogin();

        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page is not visible.");

        loginPage.signup(name, email);
        Assert.assertTrue(loginPage.isEnterAccountInfoVisible(), "Enter Account Information page not visible.");

        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();

        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("ACCOUNT CREATED"),
                "Account created message not displayed.");

        loginPage.clickContinue();
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name), "Logged in as user is not visible.");

        loginPage.clickLogout();
        Assert.assertTrue(loginPage.isLoginPageVisible(), "User is not navigated back to login page after logout.");

        loginPage.login(email, password);
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name), "User could not log in again.");

        loginPage.clickDeleteAccount();
        Assert.assertTrue(loginPage.getAccountDeletedMessage().contains("ACCOUNT DELETED"),
                "Account deleted message not displayed.");
    }
}
