package com.Harinder.Playwright.Pages;
import com.microsoft.playwright.Page;

public class ProductDetailPage {

    private final Page page;

    public ProductDetailPage(Page page) {
        this.page = page;
    }

    public boolean isProductDetailVisible() {
        page.locator(".product-information").waitFor();
        return page.locator(".product-information h2").isVisible();
    }

    public String getProductName() {
        page.locator(".product-information h2").waitFor();
        return page.locator(".product-information h2").textContent().trim();
    }

    public void setQuantity(String quantity) {
        page.locator("#quantity").fill(quantity);
    }

    public void clickAddToCart() {
        // Use force click to bypass ad overlays
        page.locator("button:has-text('Add to cart')").click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
    }

    public void clickViewCartFromPopup() {
        // Use force click to bypass ad overlays
        page.locator("text=View Cart").last().click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
    }
}
