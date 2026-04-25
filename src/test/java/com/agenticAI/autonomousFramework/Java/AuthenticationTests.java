package com.agenticAI.autonomousFramework.Java;

import com.agenticAI.autonomousFramework.Annotations.JiraTestMeta;
import com.agenticAI.autonomousFramework.Asserts.SoftAssert;
import com.agenticAI.autonomousFramework.Base.BaseTest;
import com.agenticAI.autonomousFramework.Enums.TestSeverity;
import com.agenticAI.autonomousFramework.Utils.TestDataUtil;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Negative-path authentication tests. Verifies that the login form
 * rejects bad credentials and surfaces a meaningful error.
 */
public class AuthenticationTests extends BaseTest {

    @Test(description = "Login with an invalid password is rejected with an error message")
    @JiraTestMeta(jira = "SCRUM-6", zephyr = "SCRUM-T7",
            story = "Login rejects invalid credentials",
            severity = TestSeverity.CRITICAL)
    public void verifyLoginWithInvalidPasswordRejected() {
        SoftAssert softly = new SoftAssert();

        homePage.open();
        homePage.clickSignupLogin();
        softly.assertThat("Login page is visible", loginPage.isLoginPageVisible(), is(equalTo(true)));

        loginPage.login(TestDataUtil.uniqueEmail(), "wrong-password-" + System.nanoTime());

        String errorMessage = loginPage.getLoginErrorMessage();
        softly.assertThat("Error mentions email or password",
                errorMessage.toLowerCase(), containsString("email or password"));
        softly.assertThat("User was not granted a session (still on /login)",
                page.url(), containsString("/login"));

        softly.assertAll();
    }
}
