package com.agenticAI.autonomousFramework.Java;
import com.agenticAI.autonomousFramework.Annotations.JiraTestMeta;
import com.agenticAI.autonomousFramework.Base.BaseTest;
import com.agenticAI.autonomousFramework.Enums.TestSeverity;
import com.agenticAI.autonomousFramework.Utils.TestDataUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;
import java.util.List;

public class PlaywrightTests extends BaseTest {

    @Test(priority = 1, description = "Home page loads and key nav is visible")
    @JiraTestMeta(jira = "AAF-10", zephyr = "AAF-T1", story = "Home page is reachable",
            severity = TestSeverity.CRITICAL)
    public void verifyHomePageLoads() {
        homePage.open();
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible after navigation.");
    }

    @Test(priority = 2, description = "Test Cases page is reachable from the home page")
    @JiraTestMeta(jira = "AAF-10", zephyr = "AAF-T2", story = "Home page is reachable",
            severity = TestSeverity.MAJOR)
    public void verifyTestCasesPageNavigation() {
        homePage.open();
        homePage.clickTestCases();
        Assert.assertTrue(page.url().contains("/test_cases"),
                "User should be on test cases page but URL is: " + page.url());
    }

    @Test(priority = 3, description = "Products catalog renders with at least one product")
    @JiraTestMeta(jira = "AAF-11", zephyr = "AAF-T3", story = "Products catalog displays items",
            severity = TestSeverity.CRITICAL)
    public void verifyProductsPageIsVisible() {
        homePage.open();
        homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page is not visible.");
        Assert.assertTrue(productsPage.getVisibleProductCount() > 0, "Products should be displayed on the page.");
    }

    @Test(priority = 4, description = "Catalog search returns matching results")
    @JiraTestMeta(jira = "AAF-13", zephyr = "AAF-T4", story = "User can search the catalog",
            severity = TestSeverity.MAJOR)
    public void verifySearchProduct() {
        homePage.open();
        homePage.clickProducts();

        productsPage.searchProduct("Top");

        Assert.assertTrue(productsPage.isSearchedProductsVisible(), "Searched Products heading not visible.");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(),
                "Search results should be displayed for valid product search.");
    }

    @Test(priority = 5, description = "Product detail page renders for the first product")
    @JiraTestMeta(jira = "AAF-11", zephyr = "AAF-T5", story = "Products catalog displays items",
            severity = TestSeverity.MAJOR)
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
        String expectedProductName = productDetailPage.getProductName();
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");
        List<String> names = cartPage.getCartProductNames();
        Assert.assertTrue(names.contains(expectedProductName),
                "Expected product not found in cart. Found: " + names);
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
        Assert.assertEquals(cartPage.getQuantityByRow(0), "4",
                "Product quantity in cart should match what was set.");
    }

    @Test(priority = 8)
    public void verifySubscriptionOnHomePage() {
        homePage.open();
        homePage.subscribe(TestDataUtil.uniqueEmail());

        String successMessage = homePage.getSubscriptionSuccessMessage();
        Assert.assertTrue(successMessage.contains("successfully subscribed"),
                "Subscription message mismatch. Got: " + successMessage);
    }

    @Test(priority = 9)
    public void verifyContactUsFormSubmission() {
        homePage.open();
        homePage.clickContactUs();

        Assert.assertTrue(contactUsPage.isContactUsPageVisible(), "Contact Us page is not visible.");

        String filePath = Paths.get("src/test/resources/test-upload.txt").toAbsolutePath().toString();

        // BUG: Submitting form with empty name and no subject — required fields missing
        contactUsPage.submitContactForm(
                "",
                TestDataUtil.uniqueEmail(),
                "",
                "This is a sample message from Playwright automation.",
                filePath
        );

        String successMessage = contactUsPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("Success! Your details have been submitted successfully."),
                "Contact form success message mismatch. Got: " + successMessage);
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

    @Test(priority = 11)
    public void verifyCartShowsAddedProducts() {
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");
        Assert.assertTrue(cartPage.getCartRowCount() > 0,
                "Cart should have products but it's empty.");
    }

    @Test(priority = 12)
    public void verifySearchAndViewProductDetail() {
        homePage.open();
        homePage.clickProducts();
        productsPage.searchProduct("T-Shirt");

        Assert.assertTrue(productsPage.isSearchedProductsVisible(), "Searched Products heading not visible.");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(), "No search results found.");

        productsPage.openProductDetailByIndex(0);
        Assert.assertTrue(productDetailPage.isProductDetailVisible(), "Product detail not visible.");
        String name = productDetailPage.getProductName();
        Assert.assertFalse(name.isEmpty(), "Product name should not be empty.");
    }

    @Test(priority = 13)
    public void verifyCartPersistsAfterNavigation() {
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);

        productDetailPage.setQuantity("3");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page is not visible.");

        // Navigate away and come back
        homePage.open();
        homePage.clickCart();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page not visible after re-navigation.");
        Assert.assertEquals(cartPage.getQuantityByRow(0), "3",
                "Quantity should persist after navigation.");
    }

    @Test(priority = 14)
    public void verifySignupWithMissingDetails() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        homePage.open();
        homePage.clickSignupLogin();

        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page is not visible.");

        loginPage.signup(name, email);
        Assert.assertTrue(loginPage.isEnterAccountInfoVisible(), "Enter Account Info page not visible.");

        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();

        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("ACCOUNT CREATED"),
                "Account should have been created.");
    }

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

    @Test(priority = 16)
    public void verifyLoginWithInvalidCredentials() {
        homePage.open();
        homePage.clickSignupLogin();

        Assert.assertTrue(loginPage.isLoginPageVisible(), "Login page is not visible.");

        loginPage.login("nonexistent_user_xyz@fake.com", "wrongPassword123");

        Assert.assertTrue(loginPage.getLoginErrorMessage().contains("incorrect"),
                "Expected invalid credentials error message.");
    }

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

        Assert.assertEquals(cartPage.getCartRowCount(), 2,
                "Cart should contain the correct number of products added.");
    }

    @Test(priority = 18)
    public void verifySubscriptionWithEmptyEmail() {
        homePage.open();
        homePage.subscribe("");

        Assert.assertFalse(homePage.isSubscriptionSuccessMessageVisible(),
                "Subscription should not succeed for empty email.");
    }

    // ══════════════════════════════════════════════════════════
    // COMPLEX SCENARIOS — multi-page flows with subtle bugs
    // ══════════════════════════════════════════════════════════

    @Test(priority = 19)
    public void verifyRegisterAddProductsAndVerifyCartPersistsAfterLogin() {
        // Flow: Register → Logout → Add product to cart as guest → Login → Verify cart still has the product
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        // Step 1: Register a new user
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup(name, email);
        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();
        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("ACCOUNT CREATED"),
                "Account should be created.");
        loginPage.clickContinue();

        // Step 2: Logout
        loginPage.clickLogout();

        // Step 3: Add a product to cart as guest
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);
        String expectedProduct = productDetailPage.getProductName();
        productDetailPage.setQuantity("2");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be visible.");

        // Step 4: Now login with the registered user
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.login(email, password);

        // Step 5: Navigate to cart and verify product is still there
        homePage.clickCart();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart should be visible after login.");
        List<String> cartProducts = cartPage.getCartProductNames();
        Assert.assertTrue(cartProducts.contains(expectedProduct),
                "Product added as guest should persist after login. Cart has: " + cartProducts);

        // Cleanup: delete account
        loginPage.clickDeleteAccount();
    }

    @Test(priority = 20)
    public void verifySearchAddToCartAndCrossCheckProductDetails() {
        // Flow: Search "Dress" → open result → get name → add to cart → go to cart → verify name matches
        homePage.open();
        homePage.clickProducts();

        productsPage.searchProduct("Dress");
        Assert.assertTrue(productsPage.isSearchedProductsVisible(), "Search results heading not visible.");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(), "No search results for 'Dress'.");

        // Open first search result and capture its name
        productsPage.openProductDetailByIndex(0);
        Assert.assertTrue(productDetailPage.isProductDetailVisible(), "Product detail not visible.");
        String expectedName = productDetailPage.getProductName();

        productDetailPage.setQuantity("1");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page not visible.");

        Assert.assertEquals(cartPage.getCartRowCount(), 1,
                "Cart should have the correct number of products.");

        List<String> cartNames = cartPage.getCartProductNames();
        Assert.assertTrue(cartNames.contains(expectedName),
                "Cart should contain the searched product. Found: " + cartNames);
    }

    @Test(priority = 21)
    public void verifyDuplicateRegistrationShowsError() {
        // Flow: Register user → Logout → Try to signup again with SAME email → Should show error
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        // Register first time
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup(name, email);
        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();
        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("ACCOUNT CREATED"),
                "First registration should succeed.");
        loginPage.clickContinue();
        loginPage.clickLogout();

        // Try to register again with the same email
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup("DuplicateUser", email);
        Assert.assertTrue(page.locator("text=Email Address already exist!").isVisible(),
                "Should show duplicate email error but signup succeeded with a different email.");
    }

    @Test(priority = 22)
    public void verifyFullCheckoutFlowWithQuantityAndAccountCleanup() {
        // Flow: Register → Add product with qty 3 → Cart → Verify qty → Logout → Login → Verify cart → Delete account
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        // Register
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup(name, email);
        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();
        loginPage.clickContinue();
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name), "Should be logged in after registration.");

        // Add product with quantity 3
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);
        String productName = productDetailPage.getProductName();
        productDetailPage.setQuantity("3");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart should be visible.");
        Assert.assertEquals(cartPage.getCartRowCount(), 1, "Cart should have 1 product row.");
        Assert.assertEquals(cartPage.getQuantityByRow(0), "3",
                "Quantity in cart should match what was set on product detail page.");

        // Logout and login again
        homePage.open();
        loginPage.clickLogout();
        homePage.clickSignupLogin();
        loginPage.login(email, password);
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name), "Should be logged in after re-login.");

        homePage.clickCart();
        List<String> cartNames = cartPage.getCartProductNames();
        Assert.assertTrue(cartNames.contains(productName),
                "Product should still be in cart after re-login. Found: " + cartNames);

        // Cleanup
        loginPage.clickDeleteAccount();
        Assert.assertTrue(loginPage.getAccountDeletedMessage().contains("ACCOUNT DELETED"),
                "Account deletion message not shown.");
    }

    @Test(priority = 23)
    public void verifyContactUsAfterNavigatingMultiplePages() {
        // Flow: Home → Products → Search → Product Detail → Home → Contact Us → Submit → Verify
        homePage.open();

        // Navigate through several pages first
        homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page not visible.");
        productsPage.searchProduct("Top");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(), "Search results not displayed.");
        productsPage.openProductDetailByIndex(0);
        Assert.assertTrue(productDetailPage.isProductDetailVisible(), "Product detail not visible.");

        // Now navigate to Contact Us
        homePage.open();
        homePage.clickContactUs();
        Assert.assertTrue(contactUsPage.isContactUsPageVisible(), "Contact Us page not visible.");

        String filePath = Paths.get("src/test/resources/test-upload.txt").toAbsolutePath().toString();

        contactUsPage.submitContactForm(
                "TestUser",
                TestDataUtil.uniqueEmail(),
                "Multi-page navigation test",
                "Verifying contact us works after browsing multiple pages.",
                filePath
        );

        String successMessage = contactUsPage.getSuccessMessage();
        Assert.assertEquals(successMessage, "Success! Your details have been submitted successfully.",
                "Contact form success message does not match. Got: " + successMessage);
    }
}
