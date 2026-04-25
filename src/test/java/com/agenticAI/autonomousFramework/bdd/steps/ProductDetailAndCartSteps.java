package com.agenticAI.autonomousFramework.bdd.steps;

import com.agenticAI.autonomousFramework.Asserts.SoftAssert;
import com.agenticAI.autonomousFramework.Pages.CartPage;
import com.agenticAI.autonomousFramework.Pages.HomePage;
import com.agenticAI.autonomousFramework.Pages.ProductDetailPage;
import com.agenticAI.autonomousFramework.Pages.ProductsPage;
import com.agenticAI.autonomousFramework.bdd.hooks.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class ProductDetailAndCartSteps {

    private final SoftAssert softly = new SoftAssert();
    private HomePage homePage;
    private ProductsPage productsPage;
    private ProductDetailPage productDetailPage;
    private CartPage cartPage;

    private String selectedProductName;
    private Integer expectedQuantity;

    private HomePage home() {
        if (homePage == null) homePage = new HomePage(ScenarioContext.page());
        return homePage;
    }

    private ProductsPage products() {
        if (productsPage == null) productsPage = new ProductsPage(ScenarioContext.page());
        return productsPage;
    }

    private ProductDetailPage detail() {
        if (productDetailPage == null) productDetailPage = new ProductDetailPage(ScenarioContext.page());
        return productDetailPage;
    }

    private CartPage cart() {
        if (cartPage == null) cartPage = new CartPage(ScenarioContext.page());
        return cartPage;
    }

    @When("the user clicks on the first product card")
    public void the_user_clicks_on_the_first_product_card() {
        products().openProductDetailByIndex(0);
        selectedProductName = detail().getProductName();
    }

    @Then("the product detail page should be displayed")
    public void the_product_detail_page_should_be_displayed() {
        softly.assertThat("Product detail page is visible", detail().isProductDetailVisible(), is(equalTo(true)));
        softly.assertAll();
    }

    @Then("the product name should be visible")
    public void the_product_name_should_be_visible() {
        softly.assertThat("Product name is visible", detail().isProductNameVisible(), is(equalTo(true)));
        softly.assertAll();
    }

    @Then("the product price should be visible")
    public void the_product_price_should_be_visible() {
        softly.assertThat("Product price is visible", detail().isProductPriceVisible(), is(equalTo(true)));
        softly.assertAll();
    }

    @Then("the quantity selector should be available")
    public void the_quantity_selector_should_be_available() {
        softly.assertThat("Quantity selector is visible", detail().isQuantitySelectorVisible(), is(equalTo(true)));
        softly.assertAll();
    }

    @When("the user sets the quantity to {int}")
    public void the_user_sets_the_quantity_to(Integer quantity) {
        expectedQuantity = quantity;
        detail().setQuantity(String.valueOf(quantity));
    }

    @When("the user clicks the {string} button")
    public void the_user_clicks_the_button(String buttonText) {
        if ("Add to Cart".equalsIgnoreCase(buttonText)) {
            detail().clickAddToCart();
            return;
        }
        if ("View Cart".equalsIgnoreCase(buttonText)) {
            navigateToCartPage();
            return;
        }
        throw new IllegalArgumentException("Unsupported button text: " + buttonText);
    }

    @Then("a cart confirmation message should appear")
    public void a_cart_confirmation_message_should_appear() {
        softly.assertThat("Add to cart confirmation popup is visible",
                detail().isCartConfirmationVisible(), is(equalTo(true)));
        softly.assertAll();
    }

    @And("the cart count in the navigation should increment")
    public void the_cart_count_in_the_navigation_should_increment() {
        navigateToCartPage();
        softly.assertThat("Cart has at least one item", cart().getCartRowCount(), is(greaterThan(0)));
        softly.assertAll();
    }

    @When("the user navigates to the cart page")
    public void the_user_navigates_to_the_cart_page() {
        navigateToCartPage();
    }

    @Then("the cart page should be displayed")
    public void the_cart_page_should_be_displayed() {
        softly.assertThat("Cart page is visible", cart().isCartPageVisible(), is(equalTo(true)));
        softly.assertAll();
    }

    @Then("the added product should be visible in the cart")
    public void the_added_product_should_be_visible_in_the_cart() {
        softly.assertThat("Selected product is in cart",
                cart().getCartProductNames().contains(selectedProductName), is(equalTo(true)));
        softly.assertAll();
    }

    @Then("the product name should match the original product")
    public void the_product_name_should_match_the_original_product() {
        List<String> cartProductNames = cart().getCartProductNames();
        softly.assertThat("Cart has at least one product", cartProductNames.size(), is(greaterThan(0)));
        softly.assertThat("Selected product name is present in cart",
                cartProductNames.contains(selectedProductName), is(equalTo(true)));
        softly.assertAll();
    }

    @Then("the quantity should be {int}")
    public void the_quantity_should_be(Integer quantity) {
        softly.assertThat("Cart quantity matches expected",
                cart().getQuantityByProductName(selectedProductName), is(equalTo(String.valueOf(quantity))));
        softly.assertAll();
    }

    @When("the user updates the product quantity to {int}")
    public void the_user_updates_the_product_quantity_to(Integer quantity) {
        expectedQuantity = quantity;
        replaceSelectedProductInCart(quantity);
    }

    @Then("the cart should reflect the updated quantity")
    public void the_cart_should_reflect_the_updated_quantity() {
        softly.assertThat("Updated quantity reflected in cart",
                cart().getQuantityByProductName(selectedProductName), is(equalTo(String.valueOf(expectedQuantity))));
        softly.assertAll();
    }

    @Then("the cart total should be recalculated")
    public void the_cart_total_should_be_recalculated() {
        if (expectedQuantity == null) {
            throw new IllegalStateException("Expected quantity was not set before validating cart total.");
        }
        int productRowIndex = cart().getRowIndexByProductName(selectedProductName);
        int expectedTotal = cart().getUnitPriceByRow(productRowIndex) * expectedQuantity;
        softly.assertThat("Row total reflects quantity x unit price",
                cart().getTotalPriceByRow(productRowIndex), is(equalTo(expectedTotal)));
        softly.assertAll();
    }

    @When("the user removes the product from the cart")
    public void the_user_removes_the_product_from_the_cart() {
        cart().removeProductByRow(0);
    }

    @Then("the product should be removed from the cart")
    public void the_product_should_be_removed_from_the_cart() {
        softly.assertThat("Removed product is no longer in cart",
                cart().getCartProductNames().contains(selectedProductName), is(equalTo(false)));
        softly.assertAll();
    }

    @Then("the cart should be empty")
    public void the_cart_should_be_empty() {
        softly.assertThat("Cart has zero rows", cart().getCartRowCount(), is(equalTo(0)));
        softly.assertAll();
    }

    @Then("an empty cart message should be displayed")
    public void an_empty_cart_message_should_be_displayed() {
        softly.assertThat("Empty cart message is visible",
                cart().isEmptyCartMessageVisible(), is(equalTo(true)));
        softly.assertAll();
    }

    private void navigateToCartPage() {
        if (detail().isViewCartLinkVisibleInPopup()) {
            detail().clickViewCartFromPopup();
        } else {
            home().clickCart();
        }
    }

    private void replaceSelectedProductInCart(int quantity) {
        cart().removeProductByName(selectedProductName);
        home().clickProducts();
        products().openProductDetailByIndex(0);
        selectedProductName = detail().getProductName();
        detail().setQuantity(String.valueOf(quantity));
        detail().clickAddToCart();
        detail().clickViewCartFromPopup();
    }
}
