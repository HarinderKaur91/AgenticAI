package com.agenticAI.autonomousFramework.bdd.plugin;

import com.agenticAI.autonomousFramework.Utils.ExtentReportManager;
import com.agenticAI.autonomousFramework.Utils.LoggerUtil;
import com.agenticAI.autonomousFramework.bdd.hooks.ScenarioContext;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Page;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Cucumber plugin that logs every Gherkin step (Given/When/Then/And) to:
 *  1. The Extent Report (HTML) — with colored ✓/✗/⊘ icons per step.
 *  2. The console / log4j2 logs — readable feature/scenario/step layout.
 *
 * Registered in {@code CucumberTestNGRunner} via the {@code plugin = {...}}
 * option. Step definitions remain clean — no logging code required there.
 *
 * Console output example:
 *   ─────────────────────────────────────────────────────────────
 *   Feature: cart_page.feature
 *   Scenario: User can view cart with added items
 *   ─────────────────────────────────────────────────────────────
 *     ✓ PASS  Given the user is on the home page
 *     ✓ PASS  When the user navigates to the cart page
 *     ✗ FAIL  Then the cart page should be displayed
 *             → AssertionError: expected true but was false
 *     ⊘ SKIP  And the quantity should be 1
 *   ─────────────────────────────────────────────────────────────
 *   RESULT: FAILED (3 passed, 1 failed, 1 skipped)
 */
public class ExtentCucumberPlugin implements ConcurrentEventListener {

    // Track per scenario (thread-safe for parallel execution)
    private final Map<String, ExtentTest> scenarioTests = new ConcurrentHashMap<>();
    private final Map<String, int[]> scenarioCounts = new ConcurrentHashMap<>();
    // counts[0]=pass, counts[1]=fail, counts[2]=skip, counts[3]=other

    // ASCII separator so it renders cleanly in any Windows console (cp437/1252).
    private static final String LINE = "-------------------------------------------------------------";

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::onCaseStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onCaseFinished);
    }

    private void onCaseStarted(TestCaseStarted event) {
        String scenarioId = event.getTestCase().getId().toString();
        String scenarioName = event.getTestCase().getName();
        String featureUri = event.getTestCase().getUri().toString();
        String featureFile = featureUri.substring(featureUri.lastIndexOf('/') + 1);

        // Console / log
        LoggerUtil.info("");
        LoggerUtil.info(LINE);
        LoggerUtil.info("Feature : " + featureFile);
        LoggerUtil.info("Scenario: " + scenarioName);
        LoggerUtil.info(LINE);

        // Extent
        ExtentTest test = ExtentReportManager.getTest();
        if (test == null) {
            test = ExtentReportManager.createTest(scenarioName,
                    "Feature: " + featureFile + "<br>Scenario: " + scenarioName);
        }
        scenarioTests.put(scenarioId, test);
        scenarioCounts.put(scenarioId, new int[]{0, 0, 0, 0});
    }

    private void onStepFinished(TestStepFinished event) {
        TestStep step = event.getTestStep();
        if (!(step instanceof PickleStepTestStep)) {
            return; // Skip @Before/@After hooks
        }

        PickleStepTestStep pickleStep = (PickleStepTestStep) step;
        String scenarioId = event.getTestCase().getId().toString();
        ExtentTest test = scenarioTests.get(scenarioId);
        int[] counts = scenarioCounts.get(scenarioId);
        if (test == null) return;

        String keyword = pickleStep.getStep().getKeyword().trim();
        String text = pickleStep.getStep().getText();
        String stepText = keyword + " " + text;

        Result result = event.getResult();
        Status status = result.getStatus();

        switch (status) {
            case PASSED:
                test.pass("<b style='color:green;'>✓</b> " + escape(stepText));
                LoggerUtil.info("  [PASS] " + stepText);
                if (counts != null) counts[0]++;
                break;
            case FAILED:
                test.fail("<b style='color:red;'>✗</b> " + escape(stepText));
                LoggerUtil.error("  [FAIL] " + stepText);
                if (result.getError() != null) {
                    String err = result.getError().toString();
                    test.fail("<pre style='color:red;'>" + escape(err) + "</pre>");
                    LoggerUtil.error("         -> " + firstLine(err));
                }
                attachFailureScreenshot(test, stepText);
                if (counts != null) counts[1]++;
                break;
            case SKIPPED:
                test.skip("<b style='color:orange;'>⊘</b> " + escape(stepText) + " <i>(skipped)</i>");
                LoggerUtil.info("  [SKIP] " + stepText);
                if (counts != null) counts[2]++;
                break;
            case PENDING:
                test.warning("<b style='color:orange;'>⏸</b> " + escape(stepText) + " <i>(pending)</i>");
                LoggerUtil.warn("  [PEND] " + stepText);
                if (counts != null) counts[3]++;
                break;
            case UNDEFINED:
                test.warning("<b style='color:orange;'>?</b> " + escape(stepText) + " <i>(undefined - no step definition)</i>");
                LoggerUtil.warn("  [UNDEF] " + stepText + "  (no step definition)");
                if (counts != null) counts[3]++;
                break;
            case AMBIGUOUS:
                test.warning("<b style='color:orange;'>!</b> " + escape(stepText) + " <i>(ambiguous step definition)</i>");
                LoggerUtil.warn("  [AMBIG] " + stepText + "  (ambiguous step definition)");
                if (counts != null) counts[3]++;
                break;
            default:
                test.info(escape(stepText));
                LoggerUtil.info("  - " + stepText);
        }
    }

    private void onCaseFinished(TestCaseFinished event) {
        String scenarioId = event.getTestCase().getId().toString();
        int[] counts = scenarioCounts.remove(scenarioId);
        scenarioTests.remove(scenarioId);

        Status status = event.getResult().getStatus();
        String resultLabel;
        switch (status) {
            case PASSED:  resultLabel = "PASSED";  break;
            case FAILED:  resultLabel = "FAILED";  break;
            case SKIPPED: resultLabel = "SKIPPED"; break;
            default:      resultLabel = status.name();
        }

        String summary;
        if (counts != null) {
            summary = String.format(" (%d passed, %d failed, %d skipped)",
                    counts[0], counts[1], counts[2]);
        } else {
            summary = "";
        }

        LoggerUtil.info(LINE);
        LoggerUtil.info("RESULT: " + resultLabel + summary);
        LoggerUtil.info(LINE);
        LoggerUtil.info("");
    }

    /**
     * Captures a Playwright screenshot from the current ScenarioContext page
     * and embeds it inline at the failed step in the Extent report.
     * Saves a PNG to {@code screenshots/} as well so it can be linked from CI.
     */
    private static void attachFailureScreenshot(ExtentTest test, String stepText) {
        try {
            Page page = ScenarioContext.page();
            if (page == null || page.isClosed()) return;
            byte[] shot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            String b64 = java.util.Base64.getEncoder().encodeToString(shot);
            test.fail("Screenshot at failed step:",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(b64,
                            "failed-step-" + System.currentTimeMillis()).build());
        } catch (Exception e) {
            LoggerUtil.warn("Could not capture failure screenshot: " + e.getMessage());
        }
    }

    private static String firstLine(String s) {
        if (s == null) return "";
        int nl = s.indexOf('\n');
        return nl < 0 ? s : s.substring(0, nl);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
