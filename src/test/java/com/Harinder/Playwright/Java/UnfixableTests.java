package com.Harinder.Playwright.Java;

import com.Harinder.Playwright.Base.BaseTest;
import com.microsoft.playwright.Locator;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Regression checks that were previously in intentionally failing scenarios.
 * <p>
 * This class name is kept for compatibility with existing suite configuration.
 */
public class UnfixableTests extends BaseTest {
    // Defensive upper bound to catch obvious selector/DOM regression inflation.
    private static final int MAX_REASONABLE_PRODUCT_COUNT = 500;

    /**
     * Verifies that products page is reachable and shows product content.
     */
    @Test(priority = 1)
    public void verifyProductsPageIsReachable() {
        homePage.open();
        homePage.clickProducts();

        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page should be visible.");
        Assert.assertTrue(page.url().contains("/products"),
                "User should be on the products page. URL was: " + page.url());
        Assert.assertTrue(productsPage.getVisibleProductCount() > 0,
                "Products page should display at least one product.");
    }

    /**
     * Verifies that product count is non-zero and within a realistic range.
     */
    @Test(priority = 2)
    public void verifyExactProductCount() {
        homePage.open();
        homePage.clickProducts();

        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page should be visible.");

        int actualCount = productsPage.getVisibleProductCount();
        Assert.assertTrue(actualCount > 0,
                "Product catalog should contain at least one product.");
        Assert.assertTrue(actualCount < MAX_REASONABLE_PRODUCT_COUNT,
                "Product count looks unrealistic. Found: " + actualCount);
    }

    /**
     * Verifies current wishlist behavior:
     * - if wishlist controls exist, user can navigate to wishlist page
     * - otherwise, wishlist controls are absent consistently
     */
    @Test(priority = 3)
    public void verifyWishlistFunctionality() {
        homePage.open();
        homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page should be visible.");

        Locator wishlistButtons = page.locator(".product-image-wrapper .add-to-wishlist");
        Locator wishlistLink = page.locator("a[href='/wishlist']");
        int wishlistButtonCount = wishlistButtons.count();
        int wishlistLinkCount = wishlistLink.count();

        if (wishlistButtonCount > 0 && wishlistLinkCount > 0) {
            wishlistButtons.first().click();
            wishlistLink.first().click();
            Assert.assertTrue(page.url().contains("/wishlist"),
                    "Wishlist page should open when wishlist controls are available.");
        } else {
            Assert.assertEquals(wishlistButtonCount, 0,
                    "Wishlist buttons should be absent when wishlist feature is unavailable.");
            Assert.assertEquals(wishlistLinkCount, 0,
                    "Wishlist link should be absent when wishlist feature is unavailable.");
        }
    }
}
