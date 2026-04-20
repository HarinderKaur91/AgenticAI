package com.agenticAI.autonomousFramework.Pages;
import com.agenticAI.autonomousFramework.Utils.LoggerUtil;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

public class ProductsPage {

    private final Page page;
    private static final String BASE_URL = "https://automationexercise.com";
    private static final String POINTER_INTERCEPT_ERROR = "intercepts pointer events";
    private static final int PRODUCT_DETAIL_NAVIGATION_TIMEOUT_MS = 10000;

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
        productLink.waitFor();
        productLink.scrollIntoViewIfNeeded();
        try {
            productLink.click();
        } catch (PlaywrightException ex) {
            if (!isPointerInterceptError(ex)) {
                throw ex;
            }
            navigateDirectlyToProductDetail(resolveProductDetailHref(productLink));
            return;
        }

        if (page.url().contains("/product_details/")) {
            return;
        }

        try {
            page.waitForURL("**/product_details/*", new Page.WaitForURLOptions()
                    .setTimeout(PRODUCT_DETAIL_NAVIGATION_TIMEOUT_MS));
        } catch (PlaywrightException ex) {
            LoggerUtil.warn("Product detail URL did not load after click; using direct navigation fallback.");
            navigateDirectlyToProductDetail(resolveProductDetailHref(productLink));
        }
    }

    private String resolveProductDetailHref(Locator productLink) {
        String productDetailHref = productLink.getAttribute("href");
        if (productDetailHref == null || productDetailHref.isBlank()) {
            throw new PlaywrightException("Product detail link is missing href.");
        }
        return productDetailHref;
    }

    private void navigateDirectlyToProductDetail(String productDetailHref) {
        String productDetailUrl = productDetailHref.startsWith("http")
                ? productDetailHref
                : BASE_URL + productDetailHref;
        page.navigate(productDetailUrl);
        page.waitForURL("**/product_details/*", new Page.WaitForURLOptions()
                .setTimeout(PRODUCT_DETAIL_NAVIGATION_TIMEOUT_MS));
    }

    private boolean isPointerInterceptError(PlaywrightException ex) {
        String errorMessage = ex.getMessage();
        return errorMessage != null && errorMessage.contains(POINTER_INTERCEPT_ERROR);
    }
}
