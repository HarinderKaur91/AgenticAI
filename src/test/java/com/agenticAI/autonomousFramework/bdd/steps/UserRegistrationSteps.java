package com.agenticAI.autonomousFramework.bdd.steps;

import com.agenticAI.autonomousFramework.Asserts.SoftAssert;
import com.agenticAI.autonomousFramework.Pages.HomePage;
import com.agenticAI.autonomousFramework.Pages.LoginPage;
import com.agenticAI.autonomousFramework.Utils.TestDataUtil;
import com.agenticAI.autonomousFramework.bdd.hooks.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Steps for the user_registration.feature scenarios. The "Given the
 * user is on the home page" step is reused from {@link ProductCatalogSteps}.
 */
public class UserRegistrationSteps {

    private final SoftAssert softly = new SoftAssert();
    private HomePage homePage;
    private LoginPage loginPage;

    private String name;
    private String email;
    private String password;

    private HomePage home() {
        if (homePage == null) homePage = new HomePage(ScenarioContext.page());
        return homePage;
    }

    private LoginPage login() {
        if (loginPage == null) loginPage = new LoginPage(ScenarioContext.page());
        return loginPage;
    }

    @When("the user opens the signup/login page")
    public void open_signup_login() {
        home().clickSignupLogin();
        softly.assertThat("Login page visible",
                login().isLoginPageVisible(), is(equalTo(true)));
    }

    @And("the user signs up with a new identity")
    public void signup_with_new_identity() {
        name = TestDataUtil.uniqueName();
        email = TestDataUtil.uniqueEmail();
        password = TestDataUtil.password();
        login().signup(name, email);
        softly.assertThat("Enter Account Info page visible",
                login().isEnterAccountInfoVisible(), is(equalTo(true)));
    }

    @And("the user fills in mandatory account information")
    public void fill_account_information() {
        login().fillAccountInformation(password);
        login().clickCreateAccount();
    }

    @Then("the account-created confirmation should appear")
    public void account_created_confirmation() {
        softly.assertThat("Account created message",
                login().getAccountCreatedMessage().toUpperCase(),
                containsString("ACCOUNT CREATED"));
        softly.assertAll();
    }

    @When("the user continues to the home page")
    public void continue_to_home() {
        login().clickContinue();
    }

    @Then("the user should be greeted by name in the navigation")
    public void greeted_by_name() {
        softly.assertThat("Logged in as " + name,
                login().isLoggedInAsVisible(name), is(equalTo(true)));
        softly.assertAll();
    }

    @When("the user deletes the account")
    public void delete_account() {
        login().clickDeleteAccount();
    }

    @Then("the account-deleted confirmation should appear")
    public void account_deleted_confirmation() {
        softly.assertThat("Account deleted message",
                login().getAccountDeletedMessage().toUpperCase(),
                containsString("ACCOUNT DELETED"));
        softly.assertAll();
    }
}
