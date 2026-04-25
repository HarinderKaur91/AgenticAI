package com.agenticAI.autonomousFramework.Java;

import com.agenticAI.autonomousFramework.Annotations.JiraTestMeta;
import com.agenticAI.autonomousFramework.Asserts.SoftAssert;
import com.agenticAI.autonomousFramework.Base.BaseTest;
import com.agenticAI.autonomousFramework.Enums.TestSeverity;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/**
 * Single test that proves the catalog flow on whatever browser the
 * configuration selects. Drive the matrix from the command line:
 *
 *   mvn test -Dapp.browser=chromium -Dtest=CrossBrowserSmokeTests
 *   mvn test -Dapp.browser=firefox  -Dtest=CrossBrowserSmokeTests
 *   mvn test -Dapp.browser=webkit   -Dtest=CrossBrowserSmokeTests
 */
public class CrossBrowserSmokeTests extends BaseTest {

    @Test(description = "Catalog renders on the configured browser engine")
    @JiraTestMeta(jira = "SCRUM-9", zephyr = "SCRUM-T10",
            story = "Cross-browser smoke",
            severity = TestSeverity.MAJOR)
    public void verifyCatalogRendersOnConfiguredBrowser() {
        SoftAssert softly = new SoftAssert();

        homePage.open();
        softly.assertThat("Home reachable on " + cfg.browser(),
                homePage.isHomePageVisible(), is(equalTo(true)));

        homePage.clickProducts();
        softly.assertThat("Catalog visible on " + cfg.browser(),
                productsPage.isProductsPageVisible(), is(equalTo(true)));
        softly.assertThat("Catalog has products on " + cfg.browser(),
                productsPage.getVisibleProductCount(), is(greaterThan(0)));

        softly.assertAll();
    }
}
