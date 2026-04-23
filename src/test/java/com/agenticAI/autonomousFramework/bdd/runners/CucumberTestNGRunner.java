package com.agenticAI.autonomousFramework.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Entry point for the BDD suite. Cucumber discovers .feature files
 * under src/test/resources/features and matches them to the step
 * definitions package below.
 *
 * Run only a subset of scenarios via tag filter:
 *     mvn test -Dcucumber.filter.tags="@smoke"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.agenticAI.autonomousFramework.bdd.steps",
                "com.agenticAI.autonomousFramework.bdd.hooks"
        },
        plugin = {
                "pretty",
                "html:reports/cucumber/cucumber-report.html",
                "json:reports/cucumber/cucumber-report.json",
                "summary"
        },
        monochrome = true,
        publish = false
)
public class CucumberTestNGRunner extends AbstractTestNGCucumberTests {
}
