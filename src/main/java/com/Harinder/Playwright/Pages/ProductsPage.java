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
        String productDetailHref = productLink.getAttribute("href");
        productLink.scrollIntoViewIfNeeded();
        try {
            productLink.click();
        } catch (PlaywrightException ex) {
            if (!isPointerInterceptError(ex)) {
                throw ex;
            }
            navigateDirectlyToProductDetail(productDetailHref);
            return;
        }

        if (page.url().contains("/product_details/")) {
            return;
        }

        try {
            page.waitForURL("**/product_details/*", new Page.WaitForURLOptions().setTimeout(10000));
        } catch (PlaywrightException ex) {
            if (!isTimeoutError(ex)) {
                throw ex;
            }
            navigateDirectlyToProductDetail(productDetailHref);
        }
    }

    private void navigateDirectlyToProductDetail(String productDetailHref) {
        if (productDetailHref == null || productDetailHref.isBlank()) {
            throw new PlaywrightException("Product detail link is missing href.");
        }
        String productDetailUrl = productDetailHref.startsWith("http")
                ? productDetailHref
                : "https://automationexercise.com" + productDetailHref;
        page.navigate(productDetailUrl);
        page.waitForURL("**/product_details/*");
    }

    private boolean isPointerInterceptError(PlaywrightException ex) {
        String errorMessage = ex.getMessage();
        return errorMessage != null && errorMessage.contains(POINTER_INTERCEPT_ERROR);
    }

    private boolean isTimeoutError(PlaywrightException ex) {
        String errorMessage = ex.getMessage();
        return errorMessage != null && errorMessage.contains("Timeout");
    }
}
