package com.Harinder.Playwright.Java;

import com.Harinder.Playwright.Base.BaseTest;
import com.Harinder.Playwright.Utils.TestDataUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;
import java.util.List;

public class LocatorChallengeTests extends BaseTest {

    /**
     * SCENARIO 1: Full Registration with 20 broken locators
     *
     * Every single locator in this signup flow is wrong.
     * The data-qa attributes use camelCase/kebab-case variants instead of the actual
     * snake_case attribute values on the page. The gender radio, create-account button,
     * continue button, account-created heading, and delete-account link are all wrong.
     * Agent must inspect the DOM to discover every correct selector.
     *
     * Broken locators (20):
     *   - signup-username, signup-mail, signup-btn (3 form fields)
     *   - #gender1 (missing id_ prefix)
     *   - passwd, day, month, year (4 account info fields)
     *   - firstname, lastname, company-name, street-address, street-address-2 (5 address fields)
     *   - country-select, province, city-name, zip, phone (5 more address fields)
     *   - create-btn, account-created-msg, continue-btn (3 action elements)
     *   - /remove_account (wrong href path)
     */
    @Test(priority = 1)
    public void verifySignupWithBrokenLocators() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        homePage.open();
        homePage.clickSignupLogin();

        page.locator("input[data-qa='signup-name']").fill(name);
        page.locator("input[data-qa='signup-email']").fill(email);
        page.locator("button[data-qa='signup-button']").click();

        page.locator("text=Enter Account Information").waitFor();
        Assert.assertTrue(page.locator("text=Enter Account Information").isVisible());

        page.locator("#id_gender1").check();
        page.locator("input[data-qa='password']").fill(password);
        page.locator("select[data-qa='days']").selectOption("10");
        page.locator("select[data-qa='months']").selectOption("5");
        page.locator("select[data-qa='years']").selectOption("1995");

        page.locator("input[data-qa='first_name']").fill("Test");
        page.locator("input[data-qa='last_name']").fill("User");
        page.locator("input[data-qa='company']").fill("QA Corp");
        page.locator("input[data-qa='address']").fill("123 Main St");
        page.locator("input[data-qa='address2']").fill("Unit 5");
        page.locator("select[data-qa='country']").selectOption("Canada");
        page.locator("input[data-qa='state']").fill("Ontario");
        page.locator("input[data-qa='city']").fill("Toronto");
        page.locator("input[data-qa='zipcode']").fill("M5V3L9");
        page.locator("input[data-qa='mobile_number']").fill("9876543210");

        page.locator("button[data-qa='create-account']").click();

        page.locator("h2[data-qa='account-created']").waitFor();
        String msg = page.locator("h2[data-qa='account-created']").innerText().trim();
        Assert.assertTrue(msg.contains("ACCOUNT CREATED"), "Account creation failed.");

        page.locator("a[data-qa='continue-button']").click();

        page.locator("text=Logged in as").waitFor();
        Assert.assertTrue(page.locator("text=Logged in as " + name).isVisible(),
                "User should be logged in after registration.");

        page.locator("a[href='/delete_account']").click();
    }

    /**
     * SCENARIO 2: Product Search with 8 broken locators
     *
     * Navigation link uses wrong CSS class, products heading uses fabricated class name,
     * search input and button use wrong IDs, search results container uses wrong classes,
     * product detail link and product info container use wrong selectors.
     * Agent must inspect Products page and Product Detail page DOM.
     *
     * Broken locators (8):
     *   - a.nav-link[href='/products'] (nav-link class doesn't exist)
     *   - h2.products-title (fabricated class)
     *   - #product-search-input, #search-btn (wrong IDs)
     *   - h2.searched-title (fabricated class)
     *   - .product-grid .product-card (wrong container/item classes)
     *   - .product-card a.view-details (wrong link selector)
     *   - .product-info / .product-info h2.product-title (wrong detail selectors)
     */
    @Test(priority = 2)
    public void verifyProductSearchWithBrokenLocators() {
        homePage.open();

        page.locator("a[href='/products']").first().click();

        page.locator("h2:has-text('All Products')").waitFor();
        Assert.assertTrue(page.locator("h2:has-text('All Products')").isVisible(),
                "Products page heading should be visible.");

        page.locator("#search_product").fill("Blue Top");
        page.locator("#submit_search").click();

        page.locator("h2:has-text('Searched Products')").waitFor();
        Assert.assertTrue(page.locator("h2:has-text('Searched Products')").isVisible(),
                "Searched Products heading should be visible.");

        int resultCount = page.locator(".features_items .product-image-wrapper").count();
        Assert.assertTrue(resultCount > 0, "Search results should contain products.");

        page.locator("a[href*='/product_details/']").first().click();

        page.locator(".product-information").waitFor();
        Assert.assertTrue(page.locator(".product-information h2").isVisible(),
                "Product detail name should be visible.");

        String productName = page.locator(".product-information h2").textContent().trim();
        Assert.assertFalse(productName.isEmpty(), "Product name should not be empty.");
    }

    /**
     * SCENARIO 3: Add to Cart with 6 broken locators
     *
     * Quantity input, add-to-cart button, cart modal link, cart table, product name cell,
     * and quantity cell all use fabricated selectors.
     * Agent must inspect Product Detail page and Cart page DOM.
     *
     * Broken locators (6):
     *   - input#product-qty (wrong ID)
     *   - button.add-to-cart-btn (wrong class selector)
     *   - #cart-modal a.view-cart-link (wrong modal/link selectors)
     *   - table#shopping-cart (wrong table ID)
     *   - td.product-name a (wrong cell class)
     *   - td.cart-qty button (wrong cell class)
     */
    @Test(priority = 3)
    public void verifyAddToCartWithBrokenLocators() {
        homePage.open();
        homePage.clickProducts();
        productsPage.openProductDetailByIndex(0);

        String productName = productDetailPage.getProductName();

        page.locator("input#quantity").fill("4");

        page.locator("button:has-text('Add to cart')").click();

        page.locator(".modal-content a[href='/view_cart']").waitFor();
        page.locator(".modal-content a[href='/view_cart']").click();

        page.locator("#cart_info_table tbody").waitFor();
        int rows = page.locator("#cart_info_table tbody tr").count();
        Assert.assertEquals(rows, 1, "Cart should have exactly 1 row.");

        String cartProductName = page.locator("#cart_info_table tbody tr:first-child .cart_description h4 a")
                .textContent().trim();
        Assert.assertEquals(cartProductName, productName,
                "Cart product should match what was added.");

        String qty = page.locator("#cart_info_table tbody tr:first-child .cart_quantity button")
                .textContent().trim();
        Assert.assertEquals(qty, "4", "Cart quantity should be 4.");
    }

    /**
     * SCENARIO 4: Contact Us with 8 broken locators
     *
     * Navigation link uses wrong href, heading uses fabricated class, all 4 form fields
     * use wrong data-qa prefixed names, file upload uses wrong data-qa, submit button
     * uses wrong data-qa, and success message uses wrong CSS selector.
     * Agent must inspect Contact Us page DOM.
     *
     * Broken locators (8):
     *   - a.nav-link[href='/contact'] (wrong href and class — real is a[href='/contact_us'])
     *   - h2.contact-title (fabricated class — real is text=Get In Touch)
     *   - contact-name, contact-email, contact-subject, contact-message (4 wrong data-qa values)
     *   - file-upload (wrong data-qa — real is input[name='upload_file'])
     *   - contact-submit (wrong data-qa — real is 'submit-button')
     *   - div.success-message.alert-success (wrong selector — real is .status.alert.alert-success)
     */
    @Test(priority = 4)
    public void verifyContactUsWithBrokenLocators() {
        homePage.open();

        page.locator("a[href='/contact_us']").first().click();

        page.locator("text=Get In Touch").waitFor();
        Assert.assertTrue(page.locator("text=Get In Touch").isVisible(),
                "Contact Us page should display 'Get In Touch'.");

        page.locator("input[data-qa='name']").fill("Locator Tester");
        page.locator("input[data-qa='email']").fill(TestDataUtil.uniqueEmail());
        page.locator("input[data-qa='subject']").fill("Broken Locator Test");
        page.locator("textarea[data-qa='message']").fill("Testing DOM access by agent.");
        page.locator("input[name='upload_file']").setInputFiles(
                java.nio.file.Path.of("src/test/resources/test-upload.txt").toAbsolutePath());

        page.onceDialog(dialog -> dialog.accept());
        page.locator("input[data-qa='submit-button']").click();

        page.locator(".status.alert.alert-success").waitFor();
        String result = page.locator(".status.alert.alert-success").textContent().trim();
        Assert.assertTrue(result.contains("Success! Your details have been submitted successfully."),
                "Contact form success message not found. Got: " + result);
    }

    /**
     * SCENARIO 5: Login/Logout with 6 broken locators
     *
     * Login heading uses fabricated class, email/password fields and submit button use
     * wrong data-qa names, and logout link uses wrong href and class.
     * Agent must inspect Login page DOM.
     *
     * Broken locators (6):
     *   - h2.login-title (fabricated class — real is text=Login to your account)
     *   - login-user-email (wrong data-qa — real is 'login-email')
     *   - login-user-password (wrong data-qa — real is 'login-password')
     *   - login-submit (wrong data-qa — real is 'login-button')
     *   - a.nav-link[href='/signout'] (wrong class and href — real is a[href='/logout'])
     */
    @Test(priority = 5)
    public void verifyLoginLogoutWithBrokenLocators() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        homePage.open();
        homePage.clickSignupLogin();
        loginPage.signup(name, email);
        loginPage.fillAccountInformation(password);
        loginPage.clickCreateAccount();
        loginPage.clickContinue();
        loginPage.clickLogout();

        page.locator("text=Login to your account").waitFor();
        Assert.assertTrue(page.locator("text=Login to your account").isVisible(),
                "Login page should be visible after logout.");

        page.locator("input[data-qa='login-email']").fill(email);
        page.locator("input[data-qa='login-password']").fill(password);
        page.locator("button[data-qa='login-button']").click();

        page.locator("text=Logged in as").waitFor();
        Assert.assertTrue(page.locator("text=Logged in as " + name).isVisible(),
                "Should be logged in with correct name.");

        page.locator("a[href='/logout']").click();

        page.locator("text=Login to your account").waitFor();
        Assert.assertTrue(page.locator("text=Login to your account").isVisible(),
                "Should be back on login page after second logout.");

        homePage.open();
        homePage.clickSignupLogin();
        loginPage.login(email, password);
        loginPage.clickDeleteAccount();
    }

    /**
     * SCENARIO 6: Subscription with 4 broken locators
     *
     * Subscription heading, email input, subscribe button, and success message
     * all use fabricated selectors that don't exist on the page.
     * Agent must inspect the footer DOM.
     *
     * Broken locators (4):
     *   - h2.footer-title (fabricated — real heading is near #susbscribe_email)
     *   - input#newsletter-email (wrong ID — real is #susbscribe_email, note the typo in the real site)
     *   - button#subscribe-btn (wrong ID — real is #subscribe)
     *   - div.subscription-success.alert-success (wrong selector — real is .alert-success.alert)
     */
    @Test(priority = 6)
    public void verifySubscriptionWithBrokenLocators() {
        homePage.open();

        page.locator("footer").scrollIntoViewIfNeeded();

        page.locator("text=Subscription").waitFor();
        Assert.assertTrue(page.locator("text=Subscription").first().isVisible(),
                "Subscription section heading should be visible.");

        page.locator("input#susbscribe_email").fill(TestDataUtil.uniqueEmail());

        page.locator("button#subscribe").click();

        page.locator(".alert-success.alert").waitFor();
        String msg = page.locator(".alert-success.alert").textContent().trim();
        Assert.assertTrue(msg.contains("You have been successfully subscribed!"),
                "Subscription success message not found. Got: " + msg);
    }

    /**
     * SCENARIO 7: Full End-to-End Journey — combines ALL broken locators from tests 1-6
     *
     * Registration → Products → Cart → Subscription → Contact Us → Delete Account
     * Every page interaction uses wrong locators throughout. This is the ultimate stress test:
     * 30+ broken locators across 6 different pages in a single continuous flow.
     * If the agent misses even one, the entire test fails.
     *
     * Broken locators (30+):
     *   - Navigation: a.nav-link (wrong class), /register (wrong href), /contact (wrong href)
     *   - Signup: signup-username, signup-mail, signup-btn, #gender-male, passwd, day/month/year,
     *             firstname, lastname, company-name, street-address, country-select, province,
     *             city-name, zip, phone, create-btn, account-created-msg, continue-btn
     *   - Products: a.nav-link, .product-grid .product-card, a.view-details,
     *               .product-info, h2.product-title, input#product-qty, button.add-to-cart-btn,
     *               #cart-modal, table#shopping-cart, td.cart-qty
     *   - Subscription: input#newsletter-email, button#subscribe-btn, div.subscription-success
     *   - Contact: a.nav-link[href='/contact'], contact-name/email/subject/message,
     *              contact-submit, div.success-message
     *   - Deletion: /remove_account, account-removed
     */
    @Test(priority = 7)
    public void verifyFullJourneyWithBrokenLocators() {
        String name = TestDataUtil.uniqueName();
        String email = TestDataUtil.uniqueEmail();
        String password = TestDataUtil.password();

        homePage.open();
        page.locator("a[href='/login']").first().click();

        page.locator("input[data-qa='signup-name']").fill(name);
        page.locator("input[data-qa='signup-email']").fill(email);
        page.locator("button[data-qa='signup-button']").click();

        page.locator("text=Enter Account Information").waitFor();
        page.locator("#id_gender1").check();
        page.locator("input[data-qa='password']").fill(password);
        page.locator("select[data-qa='days']").selectOption("15");
        page.locator("select[data-qa='months']").selectOption("8");
        page.locator("select[data-qa='years']").selectOption("1990");
        page.locator("input[data-qa='first_name']").fill(name);
        page.locator("input[data-qa='last_name']").fill("Tester");
        page.locator("input[data-qa='company']").fill("AgenticAI");
        page.locator("input[data-qa='address']").fill("456 Oak Ave");
        page.locator("select[data-qa='country']").selectOption("United States");
        page.locator("input[data-qa='state']").fill("California");
        page.locator("input[data-qa='city']").fill("San Francisco");
        page.locator("input[data-qa='zipcode']").fill("94105");
        page.locator("input[data-qa='mobile_number']").fill("5551234567");
        page.locator("button[data-qa='create-account']").click();

        page.locator("h2[data-qa='account-created']").waitFor();
        page.locator("a[data-qa='continue-button']").click();

        page.locator("a[href='/products']").first().click();
        page.locator("a[href*='/product_details/']").first().click();

        page.locator(".product-information").waitFor();
        String productName = page.locator(".product-information h2").textContent().trim();
        page.locator("input#quantity").fill("2");
        page.locator("button:has-text('Add to cart')").click();
        page.locator(".modal-content a[href='/view_cart']").waitFor();
        page.locator(".modal-content a[href='/view_cart']").click();

        page.locator("#cart_info_table").waitFor();
        Assert.assertEquals(page.locator("#cart_info_table tbody tr").count(), 1,
                "Cart should have 1 item.");
        String cartQty = page.locator("#cart_info_table tbody tr:first-child .cart_quantity button")
                .textContent().trim();
        Assert.assertEquals(cartQty, "2", "Cart quantity should be 2.");

        page.locator("footer").scrollIntoViewIfNeeded();
        page.locator("input#susbscribe_email").fill(email);
        page.locator("button#subscribe").click();
        page.locator(".alert-success.alert").waitFor();

        homePage.open();
        page.locator("a[href='/contact_us']").first().click();
        page.locator("input[data-qa='name']").fill(name);
        page.locator("input[data-qa='email']").fill(email);
        page.locator("input[data-qa='subject']").fill("Full Journey Test");
        page.locator("textarea[data-qa='message']").fill("End to end test with broken locators.");
        page.onceDialog(dialog -> dialog.accept());
        page.locator("input[data-qa='submit-button']").click();

        page.locator(".status.alert.alert-success").waitFor();
        String successMsg = page.locator(".status.alert.alert-success").textContent().trim();
        Assert.assertTrue(successMsg.contains("Success! Your details have been submitted successfully."),
                "Contact form should succeed. Got: " + successMsg);

        homePage.open();
        page.locator("a[href='/delete_account']").click();
        page.locator("h2[data-qa='account-deleted']").waitFor();
        Assert.assertTrue(page.locator("h2[data-qa='account-deleted']").innerText().contains("ACCOUNT DELETED"),
                "Account should be deleted.");
    }
}
