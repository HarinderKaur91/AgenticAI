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

        page.locator("input[data-qa='signup-username']").fill(name);         // WRONG: correct is 'signup-name'
        page.locator("input[data-qa='signup-mail']").fill(email);            // WRONG: correct is 'signup-email'
        page.locator("button[data-qa='signup-btn']").click();                // WRONG: correct is 'signup-button'

        page.locator("text=Enter Account Information").waitFor();
        Assert.assertTrue(page.locator("text=Enter Account Information").isVisible());

        page.locator("#gender1").check();                                    // WRONG: correct is '#id_gender1'
        page.locator("input[data-qa='passwd']").fill(password);              // WRONG: correct is 'password'
        page.locator("select[data-qa='day']").selectOption("10");            // WRONG: correct is 'days'
        page.locator("select[data-qa='month']").selectOption("5");           // WRONG: correct is 'months'
        page.locator("select[data-qa='year']").selectOption("1995");         // WRONG: correct is 'years'

        page.locator("input[data-qa='firstname']").fill("Test");             // WRONG: correct is 'first_name'
        page.locator("input[data-qa='lastname']").fill("User");              // WRONG: correct is 'last_name'
        page.locator("input[data-qa='company-name']").fill("QA Corp");       // WRONG: correct is 'company'
        page.locator("input[data-qa='street-address']").fill("123 Main St"); // WRONG: correct is 'address'
        page.locator("input[data-qa='street-address-2']").fill("Unit 5");    // WRONG: correct is 'address2'
        page.locator("select[data-qa='country-select']").selectOption("Canada"); // WRONG: correct is 'country'
        page.locator("input[data-qa='province']").fill("Ontario");           // WRONG: correct is 'state'
        page.locator("input[data-qa='city-name']").fill("Toronto");          // WRONG: correct is 'city'
        page.locator("input[data-qa='zip']").fill("M5V3L9");                 // WRONG: correct is 'zipcode'
        page.locator("input[data-qa='phone']").fill("9876543210");           // WRONG: correct is 'mobile_number'

        page.locator("button[data-qa='create-btn']").click();                // WRONG: correct is 'create-account'

        page.locator("h2[data-qa='account-created-msg']").waitFor();         // WRONG: correct is 'account-created'
        String msg = page.locator("h2[data-qa='account-created-msg']").innerText().trim(); // WRONG: same
        Assert.assertTrue(msg.contains("ACCOUNT CREATED"), "Account creation failed.");

        page.locator("a[data-qa='continue-btn']").click();                   // WRONG: correct is 'continue-button'

        page.locator("text=Logged in as").waitFor();
        Assert.assertTrue(page.locator("text=Logged in as " + name).isVisible(),
                "User should be logged in after registration.");

        page.locator("a[href='/remove_account']").click();                   // WRONG: correct is '/delete_account'
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

        page.locator("a.nav-link[href='/products']").first().click();        // WRONG: correct is a[href='/products'] (no .nav-link class)

        page.locator("h2.products-title").waitFor();                         // WRONG: correct is h2:has-text('All Products')
        Assert.assertTrue(page.locator("h2.products-title").isVisible(),     // WRONG: same
                "Products page heading should be visible.");

        page.locator("#product-search-input").fill("Blue Top");              // WRONG: correct is #search_product
        page.locator("#search-btn").click();                                 // WRONG: correct is #submit_search

        page.locator("h2.searched-title").waitFor();                         // WRONG: correct is h2:has-text('Searched Products')
        Assert.assertTrue(page.locator("h2.searched-title").isVisible(),     // WRONG: same
                "Searched Products heading should be visible.");

        int resultCount = page.locator(".product-grid .product-card").count(); // WRONG: correct is .features_items .product-image-wrapper
        Assert.assertTrue(resultCount > 0, "Search results should contain products.");

        page.locator(".product-card:first-child a.view-details").click();    // WRONG: correct is a[href*='/product_details/']

        page.locator(".product-info").waitFor();                             // WRONG: correct is .product-information
        Assert.assertTrue(page.locator(".product-info h2.product-title").isVisible(), // WRONG: correct is .product-information h2
                "Product detail name should be visible.");

        String productName = page.locator(".product-info h2.product-title").textContent().trim(); // WRONG: same
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

        page.locator("input#product-qty").fill("4");                         // WRONG: correct is #quantity

        page.locator("button.add-to-cart-btn").click();                      // WRONG: correct is button:has-text('Add to cart')

        page.locator("#cart-modal a.view-cart-link").waitFor();               // WRONG: correct is .modal-content a[href='/view_cart']
        page.locator("#cart-modal a.view-cart-link").click();                 // WRONG: same

        page.locator("table#shopping-cart tbody").waitFor();                  // WRONG: correct is #cart_info_table
        int rows = page.locator("table#shopping-cart tbody tr").count();      // WRONG: correct is #cart_info_table tbody tr
        Assert.assertEquals(rows, 1, "Cart should have exactly 1 row.");

        String cartProductName = page.locator("table#shopping-cart tbody tr:first-child td.product-name a") // WRONG: correct is .cart_description h4 a
                .textContent().trim();
        Assert.assertEquals(cartProductName, productName,
                "Cart product should match what was added.");

        String qty = page.locator("table#shopping-cart tbody tr:first-child td.cart-qty button") // WRONG: correct is .cart_quantity button
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

        page.locator("a.nav-link[href='/contact']").first().click();         // WRONG: correct is a[href='/contact_us'] (no .nav-link, wrong href)

        page.locator("h2.contact-title:has-text('Get In Touch')").waitFor(); // WRONG: correct is text=Get In Touch
        Assert.assertTrue(page.locator("h2.contact-title").isVisible(),      // WRONG: same
                "Contact Us page should display 'Get In Touch'.");

        page.locator("input[data-qa='contact-name']").fill("Locator Tester");     // WRONG: correct is 'name'
        page.locator("input[data-qa='contact-email']").fill(TestDataUtil.uniqueEmail()); // WRONG: correct is 'email'
        page.locator("input[data-qa='contact-subject']").fill("Broken Locator Test");    // WRONG: correct is 'subject'
        page.locator("textarea[data-qa='contact-message']").fill("Testing DOM access by agent."); // WRONG: correct is 'message'
        page.locator("input[data-qa='file-upload']").setInputFiles(          // WRONG: correct is input[name='upload_file']
                java.nio.file.Path.of("src/test/resources/test-upload.txt").toAbsolutePath());

        page.onceDialog(dialog -> dialog.accept());
        page.locator("input[data-qa='contact-submit']").click();             // WRONG: correct is 'submit-button'

        page.locator("div.success-message.alert-success").waitFor();         // WRONG: correct is .status.alert.alert-success
        String result = page.locator("div.success-message.alert-success").textContent().trim(); // WRONG: same
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

        page.locator("h2.login-title:has-text('Login to your account')").waitFor(); // WRONG: correct is text=Login to your account
        Assert.assertTrue(page.locator("h2.login-title").isVisible(),        // WRONG: same
                "Login page should be visible after logout.");

        page.locator("input[data-qa='login-user-email']").fill(email);       // WRONG: correct is 'login-email'
        page.locator("input[data-qa='login-user-password']").fill(password);  // WRONG: correct is 'login-password'
        page.locator("button[data-qa='login-submit']").click();              // WRONG: correct is 'login-button'

        page.locator("text=Logged in as").waitFor();
        Assert.assertTrue(page.locator("text=Logged in as " + name).isVisible(),
                "Should be logged in with correct name.");

        page.locator("a.nav-link[href='/signout']").click();                 // WRONG: correct is a[href='/logout']

        page.locator("h2.login-title").waitFor();                            // WRONG: same fabricated class
        Assert.assertTrue(page.locator("h2.login-title").isVisible(),        // WRONG: same
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

        page.locator("h2.footer-title:has-text('Subscription')").waitFor();  // WRONG: fabricated class
        Assert.assertTrue(page.locator("h2.footer-title").isVisible(),       // WRONG: same
                "Subscription section heading should be visible.");

        page.locator("input#newsletter-email").fill(TestDataUtil.uniqueEmail()); // WRONG: correct is #susbscribe_email (note real site has typo)

        page.locator("button#subscribe-btn").click();                        // WRONG: correct is #subscribe

        page.locator("div.subscription-success.alert-success").waitFor();    // WRONG: correct is .alert-success.alert
        String msg = page.locator("div.subscription-success.alert-success").textContent().trim(); // WRONG: same
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
        page.locator("a.nav-link[href='/register']").first().click();        // WRONG: correct is a[href='/login'] (no .nav-link, wrong href)

        page.locator("input[data-qa='signup-username']").fill(name);         // WRONG: correct is 'signup-name'
        page.locator("input[data-qa='signup-mail']").fill(email);            // WRONG: correct is 'signup-email'
        page.locator("button[data-qa='signup-btn']").click();                // WRONG: correct is 'signup-button'

        page.locator("text=Enter Account Information").waitFor();
        page.locator("#gender-male").check();                                // WRONG: correct is #id_gender1
        page.locator("input[data-qa='passwd']").fill(password);              // WRONG: correct is 'password'
        page.locator("select[data-qa='day']").selectOption("15");            // WRONG: correct is 'days'
        page.locator("select[data-qa='month']").selectOption("8");           // WRONG: correct is 'months'
        page.locator("select[data-qa='year']").selectOption("1990");         // WRONG: correct is 'years'
        page.locator("input[data-qa='firstname']").fill(name);               // WRONG: correct is 'first_name'
        page.locator("input[data-qa='lastname']").fill("Tester");            // WRONG: correct is 'last_name'
        page.locator("input[data-qa='company-name']").fill("AgenticAI");     // WRONG: correct is 'company'
        page.locator("input[data-qa='street-address']").fill("456 Oak Ave"); // WRONG: correct is 'address'
        page.locator("select[data-qa='country-select']").selectOption("United States"); // WRONG: correct is 'country'
        page.locator("input[data-qa='province']").fill("California");        // WRONG: correct is 'state'
        page.locator("input[data-qa='city-name']").fill("San Francisco");    // WRONG: correct is 'city'
        page.locator("input[data-qa='zip']").fill("94105");                  // WRONG: correct is 'zipcode'
        page.locator("input[data-qa='phone']").fill("5551234567");           // WRONG: correct is 'mobile_number'
        page.locator("button[data-qa='create-btn']").click();                // WRONG: correct is 'create-account'

        page.locator("h2[data-qa='account-created-msg']").waitFor();         // WRONG: correct is 'account-created'
        page.locator("a[data-qa='continue-btn']").click();                   // WRONG: correct is 'continue-button'

        page.locator("a.nav-link[href='/products']").first().click();        // WRONG: correct is a[href='/products']
        page.locator(".product-grid .product-card:first-child a.view-details").click(); // WRONG: correct is a[href*='/product_details/']

        page.locator(".product-info").waitFor();                             // WRONG: correct is .product-information
        String productName = page.locator(".product-info h2.product-title").textContent().trim(); // WRONG: correct is .product-information h2
        page.locator("input#product-qty").fill("2");                         // WRONG: correct is #quantity
        page.locator("button.add-to-cart-btn").click();                      // WRONG: correct is button:has-text('Add to cart')
        page.locator("#cart-modal a.view-cart-link").waitFor();               // WRONG: correct is .modal-content a[href='/view_cart']
        page.locator("#cart-modal a.view-cart-link").click();                 // WRONG: same

        page.locator("table#shopping-cart").waitFor();                       // WRONG: correct is #cart_info_table
        Assert.assertEquals(page.locator("table#shopping-cart tbody tr").count(), 1, // WRONG: same
                "Cart should have 1 item.");
        String cartQty = page.locator("table#shopping-cart tbody tr:first-child td.cart-qty button") // WRONG: correct is .cart_quantity button
                .textContent().trim();
        Assert.assertEquals(cartQty, "2", "Cart quantity should be 2.");

        page.locator("footer").scrollIntoViewIfNeeded();
        page.locator("input#newsletter-email").fill(email);                  // WRONG: correct is #susbscribe_email
        page.locator("button#subscribe-btn").click();                        // WRONG: correct is #subscribe
        page.locator("div.subscription-success.alert-success").waitFor();    // WRONG: correct is .alert-success.alert

        homePage.open();
        page.locator("a.nav-link[href='/contact']").first().click();         // WRONG: correct is a[href='/contact_us']
        page.locator("input[data-qa='contact-name']").fill(name);            // WRONG: correct is 'name'
        page.locator("input[data-qa='contact-email']").fill(email);          // WRONG: correct is 'email'
        page.locator("input[data-qa='contact-subject']").fill("Full Journey Test"); // WRONG: correct is 'subject'
        page.locator("textarea[data-qa='contact-message']").fill("End to end test with broken locators."); // WRONG: correct is 'message'
        page.onceDialog(dialog -> dialog.accept());
        page.locator("input[data-qa='contact-submit']").click();             // WRONG: correct is 'submit-button'

        page.locator("div.success-message.alert-success").waitFor();         // WRONG: correct is .status.alert.alert-success
        String successMsg = page.locator("div.success-message.alert-success").textContent().trim(); // WRONG: same
        Assert.assertTrue(successMsg.contains("Success! Your details have been submitted successfully."),
                "Contact form should succeed. Got: " + successMsg);

        homePage.open();
        page.locator("a[href='/remove_account']").click();                   // WRONG: correct is a[href='/delete_account']
        page.locator("h2[data-qa='account-removed']").waitFor();             // WRONG: correct is 'account-deleted'
        Assert.assertTrue(page.locator("h2[data-qa='account-removed']").innerText().contains("ACCOUNT DELETED"), // WRONG: same
                "Account should be deleted.");
    }
}
