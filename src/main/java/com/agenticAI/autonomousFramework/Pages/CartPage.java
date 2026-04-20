package com.agenticAI.autonomousFramework.Pages;
import com.microsoft.playwright.Page;
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
}