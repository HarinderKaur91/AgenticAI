package com.agenticAI.autonomousFramework.bdd.steps;

import com.agenticAI.autonomousFramework.Asserts.SoftAssert;
import com.agenticAI.autonomousFramework.Pages.HomePage;
import com.agenticAI.autonomousFramework.Utils.TestDataUtil;
import com.agenticAI.autonomousFramework.bdd.hooks.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.Matchers.containsString;

public class SubscriptionSteps {

    private final SoftAssert softly = new SoftAssert();
    private HomePage homePage;

    private HomePage home() {
        if (homePage == null) homePage = new HomePage(ScenarioContext.page());
        return homePage;
    }

    @When("the user subscribes with a unique email from the footer")
    public void subscribe_with_unique_email() {
        home().subscribe(TestDataUtil.uniqueEmail());
    }

    @Then("a subscription success message should be displayed")
    public void subscription_success() {
        softly.assertThat("Subscription success message text",
                home().getSubscriptionSuccessMessage(),
                containsString("successfully subscribed"));
        softly.assertAll();
    }
}
