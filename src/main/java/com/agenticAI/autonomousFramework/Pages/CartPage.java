package com.agenticAI.autonomousFramework.Pages;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.util.ArrayList;
import java.util.List;

public class CartPage {

    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public boolean isCartPageVisible() {
        page.locator("#cart_info_table").waitFor();
        return page.locator("#cart_info_table").isVisible();
    }

    public int getCartRowCount() {
        return page.locator("#cart_info_table tbody tr").count();
    }

    public List<String> getCartProductNames() {
        List<String> names = new ArrayList<>();
        int count = page.locator("#cart_info_table tbody tr").count();

        for (int i = 0; i < count; i++) {
            String name = page.locator("#cart_info_table tbody tr")
                    .nth(i)
                    .locator(".cart_description h4 a")
                    .textContent()
                    .trim();
            names.add(name);
        }
        return names;
    }

    public String getQuantityByRow(int rowIndex) {
        return page.locator("#cart_info_table tbody tr")
                .nth(rowIndex)
                .locator(".cart_quantity button")
                .textContent()
                .trim();
    }

    public String getQuantityByProductName(String productName) {
        return getQuantityByRow(findRowIndexByProductName(productName));
    }

    public void removeProductByRow(int rowIndex) {
        Locator row = page.locator("#cart_info_table tbody tr").nth(rowIndex);
        row.locator(".cart_quantity_delete").click(new Locator.ClickOptions().setForce(true));
        row.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED));
    }

    public void removeProductByName(String productName) {
        removeProductByRow(findRowIndexByProductName(productName));
    }

    public int getRowIndexByProductName(String productName) {
        return findRowIndexByProductName(productName);
    }

    public boolean isEmptyCartMessageVisible() {
        Locator emptyCart = page.locator("#empty_cart");
        return emptyCart.count() > 0 && emptyCart.first().isVisible();
    }

    public int getUnitPriceByRow(int rowIndex) {
        String priceText = page.locator("#cart_info_table tbody tr")
                .nth(rowIndex)
                .locator(".cart_price p")
                .textContent()
                .trim();
        return parseCurrencyAmount(priceText);
    }

    public int getTotalPriceByRow(int rowIndex) {
        String totalText = page.locator("#cart_info_table tbody tr")
                .nth(rowIndex)
                .locator(".cart_total p")
                .textContent()
                .trim();
        return parseCurrencyAmount(totalText);
    }

    private int parseCurrencyAmount(String text) {
        // AutomationExercise cart prices are whole-number rupee strings such as "Rs. 500".
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Unable to parse currency amount from: " + text
                    + " (no numeric digits found).");
        }
        return Integer.parseInt(digits);
    }

    private int findRowIndexByProductName(String productName) {
        int count = page.locator("#cart_info_table tbody tr").count();
        for (int i = 0; i < count; i++) {
            String currentName = page.locator("#cart_info_table tbody tr")
                    .nth(i)
                    .locator(".cart_description h4 a")
                    .textContent()
                    .trim();
            if (currentName.equals(productName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Product not found in cart: " + productName
                + ". Available products: " + getCartProductNames());
    }
}
