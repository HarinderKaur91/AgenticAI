package com.agenticAI.autonomousFramework.Java;
import com.agenticAI.autonomousFramework.Base.BaseTest;
import com.agenticAI.autonomousFramework.Utils.TestDataUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;
import java.util.List;

public class ComplexPlaywrightTests extends BaseTest {

    /**
     * SCENARIO 1: Layered Bug — Cart persistence across login session
     *
     * Flow: Register → Logout → Browse products as guest → Add to cart with qty 2
     *       → Login with registered account → Verify cart persists with correct product and quantity
     *       → Delete account
     *
     * Complexity: The login step uses a freshly generated email (not the registered one),
     *             so the user is never actually logged in. Every assertion after that
     *             operates on a guest session. The cart quantity assertion also checks
     *             row index 1 instead of 0, which will fail even if login were correct.
     *             Agent must fix BOTH — and the second bug is invisible until the first is fixed.
     */
    @Test(priority = 1)
    public void verifyCartPersistsAcrossLoginSession() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup(name, email);
        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();
        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("ACCOUNT CREATED"),
                "Registration should succeed.");
        loginPage.clickContinue();
        loginPage.clickLogout();

        // Browse and add product as guest
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);
        String expectedProduct = productDetailPage.getProductName();
        productDetailPage.setQuantity("2");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be visible.");

        // Login with registered account
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.login(email, password);

        // Verify cart after login
        homePage.clickCart();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart should be visible after login.");
        List<String> cartProducts = cartPage.getCartProductNames();
        Assert.assertTrue(cartProducts.contains(expectedProduct),
                "Product should persist in cart after login. Found: " + cartProducts);
        Assert.assertEquals(cartPage.getQuantityByRow(0), "2",
                "Quantity should be 2 for the added product.");

        loginPage.clickDeleteAccount();
    }

    /**
     * SCENARIO 2: Order-of-Operations — Delete account before verifying cart
     *
     * Flow: Register → Add product → Go to cart → Delete account → Then try to verify cart contents
     *
     * Complexity: The deleteAccount call happens BEFORE the cart verification assertions.
     *             After account deletion, the page redirects away from cart. The subsequent
     *             cartPage assertions will fail with element-not-found because the page
     *             context is on the "Account Deleted" page. Agent must restructure the
     *             entire test — move the cart assertions before the delete, and the delete
     *             to the end. A naive fix (just fixing assertions) won't work.
     */
    @Test(priority = 2)
    public void verifyCartContentsBeforeAccountDeletion() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup(name, email);
        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();
        loginPage.clickContinue();
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name), "Should be logged in.");

        // Add product
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);
        String productName = productDetailPage.getProductName();
        productDetailPage.setQuantity("3");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart should be visible.");

        Assert.assertEquals(cartPage.getCartRowCount(), 1, "Cart should have 1 product.");
        List<String> names = cartPage.getCartProductNames();
        Assert.assertTrue(names.contains(productName),
                "Product should be in cart. Found: " + names);
        Assert.assertEquals(cartPage.getQuantityByRow(0), "3",
                "Quantity should match what was added.");

        loginPage.clickDeleteAccount();
        Assert.assertTrue(loginPage.getAccountDeletedMessage().contains("ACCOUNT DELETED"),
                "Account deletion message not displayed.");
    }

    /**
     * SCENARIO 3: Cascading Failure — Wrong search term causes silent wrong product
     *
     * Flow: Search "Blue Top" → Open first result → Capture name → Add to cart
     *       → Verify cart has "Blue Top"
     *
     * Complexity: Searches for "Blue" instead of "Blue Top". The search returns results,
     *             the first one might not be "Blue Top". The name is captured dynamically so
     *             the first assertion passes. But the final assertEquals checks against the
     *             hardcoded "Blue Top" which may not match the actual first search result.
     *             Agent must also fix the search term AND the assertion approach — either
     *             use the dynamic name or fix the search to be exact.
     *             Additionally, the quantity is set to "1" but the assertion checks for "2".
     */
    @Test(priority = 3)
    public void verifySearchProductAddToCartAndValidateDetails() {
        homePage.open();
        homePage.clickProducts();

        productsPage.searchProduct("Blue Top");
        Assert.assertTrue(productsPage.isSearchedProductsVisible(), "Search results should be visible.");
        Assert.assertTrue(productsPage.areSearchResultsDisplayed(), "Results should be displayed.");

        productsPage.openProductDetailByIndex(0);
        Assert.assertTrue(productDetailPage.isProductDetailVisible(), "Product detail should be visible.");
        String actualName = productDetailPage.getProductName();

        productDetailPage.setQuantity("1");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be visible.");

        List<String> cartNames = cartPage.getCartProductNames();
        Assert.assertEquals(cartNames.get(0), actualName,
                "Cart should contain the searched product. Found: " + cartNames);

        Assert.assertEquals(cartPage.getQuantityByRow(0), "1",
                "Cart quantity should match what was entered on product detail page.");
    }

    /**
     * SCENARIO 4: Ambiguous Fix — Duplicate registration with stale variable
     *
     * Flow: Register → Logout → Re-register with same email → Expect "already exists" error
     *       → Login with original credentials → Verify logged in → Submit contact form → Delete account
     *
     * Complexity: Three layered bugs:
     *   1. Second signup uses uniqueEmail() (new email) instead of the saved `email` variable
     *      — so the "already exists" error never appears
     *   2. After the "failed" duplicate check, it tries to login but uses `name` in the
     *      email field instead of `email`
     *   3. Contact form submission uses empty message body, which may cause the success
     *      assertion to fail
     *   Agent must fix all three in sequence — each one only becomes visible after the previous is fixed.
     */
    @Test(priority = 4)
    public void verifyDuplicateRegistrationThenLoginAndContactUs() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        // Register
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup(name, email);
        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();
        Assert.assertTrue(loginPage.getAccountCreatedMessage().contains("ACCOUNT CREATED"),
                "Registration should succeed.");
        loginPage.clickContinue();
        loginPage.clickLogout();

        // Attempt duplicate registration
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup("DuplicateUser", email);

        Assert.assertTrue(page.locator("text=Email Address already exist!").isVisible(),
                "Duplicate email error should be displayed.");

        // Login with original credentials
        homePage.open();
        homePage.clickSignupLogin();
        loginPage.login(email, password);
        Assert.assertTrue(loginPage.isLoggedInAsVisible(name),
                "Should be logged in after using original credentials.");

        // Submit contact form while logged in
        homePage.clickContactUs();
        Assert.assertTrue(contactUsPage.isContactUsPageVisible(), "Contact Us page should be visible.");

        String filePath = Paths.get("src/test/resources/test-upload.txt").toAbsolutePath().toString();
        contactUsPage.submitContactForm(
                name,
                email,
                "Duplicate Registration Test",
                "Please validate duplicate registration handling and contact flow.",
                filePath
        );

        String successMessage = contactUsPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("Success! Your details have been submitted successfully."),
                "Contact form should succeed. Got: " + successMessage);

        // Cleanup
        homePage.open();
        loginPage.clickDeleteAccount();
    }

    /**
     * SCENARIO 5: Cross-Page State Corruption — Multi-page journey with compounding errors
     *
     * Flow: Home → Products → Add product 0 to cart → Home → Products → Add product 1 to cart
     *       → Cart → Verify 2 products → Subscribe → Go back to cart → Verify cart unchanged
     *       → Contact Us → Submit → Verify success
     *
     * Complexity: Four independent bugs hidden across the long flow:
     *   1. After adding second product, navigates to Home instead of clicking "View Cart"
     *      — then asserts on CartPage which isn't loaded
     *   2. Subscribe is called with a hardcoded invalid email format "notanemail"
     *   3. After subscription, navigates to cart but asserts getCartRowCount() == 3 (should be 2)
     *   4. Contact form uses null for filePath AND asserts the wrong success message string
     *   Agent must trace through 12+ steps to find all 4 bugs. Fixing any subset still leaves failures.
     */
    @Test(priority = 5)
    public void verifyMultiPageJourneyWithCartSubscriptionAndContactUs() {
        homePage.open();

        // Add first product
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);
        String product1 = productDetailPage.getProductName();
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart should show after first add.");

        // Add second product
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(1);
        String product2 = productDetailPage.getProductName();
        productDetailPage.clickAddToCart();

        productDetailPage.clickViewCartFromPopup();

        // Try to assert on cart page — but we're on home page
        Assert.assertEquals(cartPage.getCartRowCount(), 2,
                "Cart should have 2 products after adding two items.");

        List<String> cartNames = cartPage.getCartProductNames();
        Assert.assertTrue(cartNames.contains(product1), "First product should be in cart.");
        Assert.assertTrue(cartNames.contains(product2), "Second product should be in cart.");

        // Subscribe with invalid email
        homePage.subscribe(TestDataUtil.uniqueEmail());
        Assert.assertTrue(homePage.isSubscriptionSuccessMessageVisible(),
                "Subscription should succeed.");

        // Navigate to cart and verify it's unchanged
        homePage.clickCart();
        Assert.assertEquals(cartPage.getCartRowCount(), 2,
                "Cart should still have the same products after subscription.");

        // Contact Us
        homePage.open();
        homePage.clickContactUs();
        Assert.assertTrue(contactUsPage.isContactUsPageVisible(), "Contact Us should be visible.");

        contactUsPage.submitContactForm(
                "Journey Tester",
                TestDataUtil.uniqueEmail(),
                "Multi-page journey",
                "Testing state across multiple pages.",
                Paths.get("src/test/resources/test-upload.txt").toAbsolutePath().toString()
        );

        String successMessage = contactUsPage.getSuccessMessage();
        Assert.assertEquals(successMessage, "Success! Your details have been submitted successfully.",
                "Contact form success message mismatch. Got: " + successMessage);
    }
}
