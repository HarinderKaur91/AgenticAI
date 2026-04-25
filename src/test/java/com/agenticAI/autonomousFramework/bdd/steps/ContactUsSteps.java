package com.agenticAI.autonomousFramework.bdd.steps;

import com.agenticAI.autonomousFramework.Asserts.SoftAssert;
import com.agenticAI.autonomousFramework.Pages.ContactUsPage;
import com.agenticAI.autonomousFramework.Pages.HomePage;
import com.agenticAI.autonomousFramework.Utils.TestDataUtil;
import com.agenticAI.autonomousFramework.bdd.hooks.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.nio.file.Paths;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ContactUsSteps {

    private final SoftAssert softly = new SoftAssert();
    private HomePage homePage;
    private ContactUsPage contactUsPage;

    private HomePage home() {
        if (homePage == null) homePage = new HomePage(ScenarioContext.page());
        return homePage;
    }

    private ContactUsPage contact() {
        if (contactUsPage == null) contactUsPage = new ContactUsPage(ScenarioContext.page());
        return contactUsPage;
    }

    @When("the user opens the Contact Us page")
    public void open_contact_us() {
        home().clickContactUs();
        softly.assertThat("Contact Us page visible",
                contact().isContactUsPageVisible(), is(equalTo(true)));
    }

    @And("the user submits the contact form with a sample attachment")
    public void submit_contact_form() {
        String filePath = Paths.get("src/test/resources/test-upload.txt").toAbsolutePath().toString();
        contact().submitContactForm(
                "BDD Bot",
                TestDataUtil.uniqueEmail(),
                "Automated message from Cucumber",
                "Hello from the BDD suite -- automated submission.",
                filePath
        );
    }

    @Then("a success confirmation message should be displayed")
    public void success_confirmation() {
        softly.assertThat("Success message contains expected text",
                contact().getSuccessMessage(),
                containsString("Success! Your details have been submitted successfully."));
        softly.assertAll();
    }
}
