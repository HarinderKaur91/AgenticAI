package com.agenticAI.autonomousFramework.Pages;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

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

    public boolean isProductNameVisible() {
        return page.locator(".product-information h2").isVisible();
    }

    public boolean isProductPriceVisible() {
        return page.locator(".product-information span span").first().isVisible();
    }

    public boolean isQuantitySelectorVisible() {
        return page.locator("#quantity").isVisible();
    }

    public void setQuantity(String quantity) {
        page.locator("#quantity").fill(quantity);
    }

    public void clickAddToCart() {
        // Use force click to bypass ad overlays
        page.locator("button:has-text('Add to cart')").click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
    }

    public void clickViewCartFromPopup() {
        Locator viewCartLink = page.locator(".modal-content a[href='/view_cart']");
        viewCartLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        // Use force click to bypass ad overlays
        viewCartLink.click(new Locator.ClickOptions().setForce(true));
    }

    public boolean isCartConfirmationVisible() {
        return page.locator(".modal-content").isVisible();
    }

    public boolean isViewCartLinkVisibleInPopup() {
        Locator viewCartLink = page.locator(".modal-content a[href='/view_cart']");
        return viewCartLink.count() > 0 && viewCartLink.first().isVisible();
    }
}
