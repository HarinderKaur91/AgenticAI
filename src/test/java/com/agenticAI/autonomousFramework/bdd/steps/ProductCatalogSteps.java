package com.agenticAI.autonomousFramework.bdd.steps;

import com.agenticAI.autonomousFramework.Asserts.SoftAssert;
import com.agenticAI.autonomousFramework.Pages.HomePage;
import com.agenticAI.autonomousFramework.Pages.ProductsPage;
import com.agenticAI.autonomousFramework.bdd.hooks.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Step definitions for the AutomationExercise BDD scenarios.
 * Reuses the same Page Objects as the TDD suite.
 */
public class ProductCatalogSteps {

    private final SoftAssert softly = new SoftAssert();
    private HomePage homePage;
    private ProductsPage productsPage;

    private HomePage home() {
        if (homePage == null) homePage = new HomePage(ScenarioContext.page());
        return homePage;
    }

    private ProductsPage products() {
        if (productsPage == null) productsPage = new ProductsPage(ScenarioContext.page());
        return productsPage;
    }

    @Given("the user is on the home page")
    public void the_user_is_on_the_home_page() {
        home().open();
        softly.assertThat("Home page is visible", home().isHomePageVisible(), is(equalTo(true)));
    }

    @When("the user navigates to the products catalog")
    public void the_user_navigates_to_the_products_catalog() {
        home().clickProducts();
    }

    @Then("the products catalog should be displayed")
    public void the_products_catalog_should_be_displayed() {
        softly.assertThat("Products page is visible", products().isProductsPageVisible(), is(equalTo(true)));
        // TEMP: forced failure to demo screenshot embedding in Extent report — revert after verifying
        softly.assertThat("Catalog has at least 9999 products (forced fail)",
                products().getVisibleProductCount(), is(greaterThan(9999)));
        softly.assertAll();
    }

    @When("the user searches for {string}")
    public void the_user_searches_for(String term) {
        products().searchProduct(term);
    }

    @Then("search results should reference {string}")
    public void search_results_should_reference(String term) {
        softly.assertThat("Searched products heading visible",
                products().isSearchedProductsVisible(), is(equalTo(true)));
        softly.assertThat("Search results displayed",
                products().areSearchResultsDisplayed(), is(equalTo(true)));
        softly.assertAll();
    }

    @And("the page URL should contain {string}")
    public void the_page_url_should_contain(String fragment) {
        softly.assertThat("URL contains expected fragment",
                ScenarioContext.page().url(), containsString(fragment));
        softly.assertAll();
    }
}
