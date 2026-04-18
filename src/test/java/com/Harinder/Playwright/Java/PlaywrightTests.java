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
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible after navigation.");
    }

    @Test(priority = 2)
    public void verifyTestCasesPageNavigation() {
        homePage.open();
        homePage.clickTestCases();
        Assert.assertTrue(page.url().contains("/test_cases"),
                "User should be on test cases page but URL is: " + page.url());
    }

    @Test(priority = 3)
    public void verifyProductsPageIsVisible() {
        homePage.open();
        homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page is not visible.");
        Assert.assertTrue(productsPage.getVisibleProductCount() > 0, "Products should be displayed on the page.");
    }

    @Test(priority = 4)
    public void verifySearchProduct() {
        homePage.open();
        homePage.clickProducts();

        productsPage.searchProduct("Top");

        Assert.assertTrue(productsPage.isSearchedProductsVisible(), "Searched Products heading not visible.");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(),
                "Search results should be displayed for valid product search.");
    }

    @Test(priority = 5)
    public void verifyFirstProductDetailPage() {
        homePage.open();
        homePage.clickProducts();

        productsPage.openProductDetailByIndex(0);

        Assert.assertTrue(productDetailPage.isProductDetailVisible(), "Product detail page is not visible.");
        Assert.assertFalse(productDetailPage.getProductName().isEmpty(), "Product name is empty.");
    }

    // BUG 6: Comparing product name with hardcoded wrong name "InvalidProduct"
    @Test(priority = 6)
    public void verifyAddSingleProductToCart() {
        homePage.open();
        homePage.clickProducts();

        productsPage.openProductDetailByIndex(0);
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");
        List<String> names = cartPage.getCartProductNames();
        Assert.assertTrue(names.contains("InvalidProduct"),
                "Expected product not found in cart. Found: " + names);
    }

    // BUG 7: Setting quantity to "4" but asserting it equals "5"
    @Test(priority = 7)
    public void verifyProductQuantityInCart() {
        homePage.open();
        homePage.clickProducts();

        productsPage.openProductDetailByIndex(0);

        productDetailPage.setQuantity("4");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");
        Assert.assertEquals(cartPage.getQuantityByRow(0), "5",
                "Product quantity in cart should match what was set.");
    }

    // BUG 8: Checking for wrong success message text "subscribed successfully" 
    // instead of "successfully subscribed" (word order is wrong)
    @Test(priority = 8)
    public void verifySubscriptionOnHomePage() {
        homePage.open();
        homePage.subscribe(TestDataUtil.uniqueEmail());

        String successMessage = homePage.getSubscriptionSuccessMessage();
        Assert.assertTrue(successMessage.contains("subscribed successfully"),
                "Subscription message mismatch. Got: " + successMessage);
    }

    // BUG 9: Checking for wrong success message — "submitted" vs "Submitted"
    // (wrong case sensitivity check)
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
        Assert.assertTrue(successMessage.contains("Success! Your details have been Submitted successfully."),
                "Contact form success message mismatch. Got: " + successMessage);
    }

    // BUG 10: After account creation, checking for "Account Created" instead of 
    // "ACCOUNT CREATED" (case mismatch)
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

        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("Account Created"),
                "Account created message not displayed.");

        loginPage.clickContinue();
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name), "Logged in as user is not visible.");

        loginPage.clickLogout();
        Assert.assertTrue(loginPage.isLoginPageVisible(), "User is not navigated back to login page after logout.");

        loginPage.login(email, password);
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name), "User could not log in again.");

        loginPage.clickDeleteAccount();
        Assert.assertTrue(loginPage.getAccountDeletedMessage().contains("Account Deleted"),
                "Account deleted message not displayed.");
    }

    // BUG 11: Missing step — navigates to cart without adding any product first,
    // then asserts cart has items
    @Test(priority = 11)
    public void verifyCartShowsAddedProducts() {
        homePage.open();
        homePage.clickCart();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");
        Assert.assertTrue(cartPage.getCartRowCount() > 0,
                "Cart should have products but it's empty. Forgot to add products before checking cart.");
    }

    // BUG 12: Multi-step flow with missing navigation — tries to search on products
    // page without navigating to products page first
    @Test(priority = 12)
    public void verifySearchAndViewProductDetail() {
        homePage.open();

        // Missing: homePage.clickProducts() — not on products page yet
        productsPage.searchProduct("T-Shirt");

        Assert.assertTrue(productsPage.isSearchedProductsVisible(), "Searched Products heading not visible.");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(), "No search results found.");

        productsPage.openProductDetailByIndex(0);
        Assert.assertTrue(productDetailPage.isProductDetailVisible(), "Product detail not visible.");
        String name = productDetailPage.getProductName();
        Assert.assertFalse(name.isEmpty(), "Product name should not be empty.");
    }

    // BUG 13: Adds product to cart, then navigates away and back to cart, 
    // but asserts wrong quantity — expects "2" when only 1 was added
    @Test(priority = 13)
    public void verifyCartPersistsAfterNavigation() {
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);

        productDetailPage.setQuantity("1");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");

        // Navigate away and come back
        homePage.open();
        homePage.clickCart();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page not visible after re-navigation.");
        Assert.assertEquals(cartPage.getQuantityByRow(0), "2",
                "Quantity should persist after navigation. Expected 2 but likely 1.");
    }

    // BUG 14: Signup flow that skips fillAccountInformation — clickCreateAccount 
    // will fail because the form isn't filled
    @Test(priority = 14)
    public void verifySignupWithMissingDetails() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();

        homePage.open();
        homePage.clickSignupLogin();

        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page is not visible.");

        loginPage.signup(name, email);
        Assert.assertTrue(loginPage.isEnterAccountInfoVisible(), "Enter Account Info page not visible.");

        // Missing: loginPage.fillAccountInformation(password) — form not filled!
        loginPage.clickCreateAccount();

        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("ACCOUNT CREATED"),
                "Account should have been created but form was incomplete.");
    }

    // BUG 15: Contact form submission without filling the file upload,
    // passes null file path which will cause an error
    @Test(priority = 15)
    public void verifyContactUsWithoutFileUpload() {
        homePage.open();
        homePage.clickContactUs();

        Assert.assertTrue(contactUsPage.isContactUsPageVisible(), "Contact Us page is not visible.");

        // Missing: file path is null — setInputFiles will fail
        contactUsPage.submitContactForm(
                "Harinder",
                TestDataUtil.uniqueEmail(),
                "Test Subject",
                "Test message body",
                null
        );

        String successMessage = contactUsPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("Success! Your details have been submitted successfully."),
                "Contact form should succeed even without file. Got: " + successMessage);
    }

    // BUG 16: Tries to login with unregistered credentials — login will fail
    // but test asserts success
    @Test(priority = 16)
    public void verifyLoginWithInvalidCredentials() {
        homePage.open();
        homePage.clickSignupLogin();

        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page is not visible.");

        loginPage.login("nonexistent_user_xyz@fake.com", "wrongPassword123");

        // This will fail — user doesn't exist, can't be logged in
        Assert.assertTrue(loginPage.isLoggedInAsVisible("NonExistentUser"),
                "Should show error for invalid login, not logged in state.");
    }

    // BUG 17: Complex multi-product cart flow — adds 2 products but checks for 3 rows
    @Test(priority = 17)
    public void verifyMultipleProductsInCart() {
        homePage.open();
        homePage.clickProducts();

        // Add first product
        productsPage.openProductDetailByIndex(0);
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");

        // Go back and add second product
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(1);
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        // BUG: Asserts 3 products when only 2 were added
        Assert.assertEquals(cartPage.getCartRowCount(), 3,
                "Cart should contain the correct number of products added.");
    }

    // BUG 18: Subscription test with empty email — subscribe with blank string,
    // then asserts success message appears
    @Test(priority = 18)
    public void verifySubscriptionWithEmptyEmail() {
        homePage.open();
        homePage.subscribe("");

        String successMessage = homePage.getSubscriptionSuccessMessage();
        Assert.assertTrue(successMessage.contains("successfully subscribed"),
                "Subscription should validate email. Got: " + successMessage);
    }
}
