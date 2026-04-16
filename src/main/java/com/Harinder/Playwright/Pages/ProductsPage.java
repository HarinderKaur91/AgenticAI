package com.Harinder.Playwright.Pages;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

public class ProductsPage {

    private final Page page;
    private static final String POINTER_INTERCEPT_ERROR = "intercepts pointer events";

    public ProductsPage(Page page) {
        this.page = page;
    }

    public boolean isProductsPageVisible() {
        page.locator("h2:has-text('All Products')").waitFor();
        return page.locator("h2:has-text('All Products')").isVisible();
    }

    public int getVisibleProductCount() {
        return page.locator(".features_items .product-image-wrapper").count();
    }

    public void searchProduct(String productName) {
        page.locator("#search_product").fill(productName);
        page.locator("#submit_search").click();
    }

    public boolean isSearchedProductsVisible() {
        page.locator("h2:has-text('Searched Products')").waitFor();
        return page.locator("h2:has-text('Searched Products')").isVisible();
    }

    public boolean areSearchResultsDisplayed() {
        return page.locator(".features_items .product-image-wrapper").count() > 0;
    }

    public void openProductDetailByIndex(int index) {
        Locator viewProductLinks = page.locator("a[href*='/product_details/']");
        Locator productLink = viewProductLinks.nth(index);
        productLink.scrollIntoViewIfNeeded();
        try {
            productLink.click();
        } catch (PlaywrightException ex) {
            String errorMessage = ex.getMessage();
            boolean isPointerInterceptError = errorMessage != null
                    && errorMessage.contains(POINTER_INTERCEPT_ERROR);
            if (!isPointerInterceptError) {
                throw ex;
            }
            productLink.focus();
            page.keyboard().press("Enter");
        }
    }
}
