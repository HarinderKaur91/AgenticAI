package com.agenticAI.autonomousFramework.Java;

import com.agenticAI.autonomousFramework.Base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Regression tests for core catalog and cart journeys on automationexercise.com.
 * These tests validate products page navigation, product listing visibility,
 * and add-to-cart behavior.
 */
public class UnfixableTests extends BaseTest {

    @Test(priority = 1)
    public void verifyProductsPageNavigation() {
        homePage.open();
        homePage.clickProducts();

        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page should be visible.");
        Assert.assertTrue(page.url().contains("/products"),
                "User should be on products page but URL is: " + page.url());
    }

    @Test(priority = 2)
    public void verifyExactProductCount() {
        homePage.open();
        homePage.clickProducts();

        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page should be visible.");

        int actualCount = productsPage.getVisibleProductCount();
        Assert.assertTrue(actualCount > 0,
                "Product catalog should contain at least one visible product. Found: " + actualCount);
    }

    @Test(priority = 3)
    public void verifyAddToCartFlow() {
        homePage.open();
        homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "Products page should be visible.");
        Assert.assertTrue(productsPage.getVisibleProductCount() > 0,
                "At least one product should be visible before opening details.");

        productsPage.openProductDetailByIndex(0);
        String expectedProductName = productDetailPage.getProductName();
        productDetailPage.setQuantity("1");
        productDetailPage.clickAddToCart();
        productDetailPage.clickViewCartFromPopup();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be visible.");
        Assert.assertTrue(cartPage.getCartProductNames().contains(expectedProductName),
                "Expected product was not found in the cart.");
    }
}
