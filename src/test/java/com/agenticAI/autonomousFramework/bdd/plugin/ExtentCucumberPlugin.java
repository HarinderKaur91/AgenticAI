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
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    // Collected for the emailable summary report (written at end of run).
    private final List<ScenarioRow> scenarioRows = new ArrayList<>();

    private static final class ScenarioRow {
        final String name;
        final String featurePath;
        final String status;
        final long durationMs;
        ScenarioRow(String name, String featurePath, String status, long durationMs) {
            this.name = name;
            this.featurePath = featurePath;
            this.status = status;
            this.durationMs = durationMs;
        }
    }

    // ASCII separator so it renders cleanly in any Windows console (cp437/1252).
    private static final String LINE = "-------------------------------------------------------------";

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::onCaseStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::onStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::onRunFinished);
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

        // Record row for the emailable summary report
        String featureUri = event.getTestCase().getUri().toString();
        String featurePath = featureUri.startsWith("file:/")
                ? featureUri.substring(featureUri.indexOf("src/test/resources") >= 0
                        ? featureUri.indexOf("src/test/resources") : 6)
                : featureUri;
        long durationMs = event.getResult().getDuration().toMillis();
        synchronized (scenarioRows) {
            scenarioRows.add(new ScenarioRow(
                    event.getTestCase().getName(), featurePath, resultLabel, durationMs));
        }

        LoggerUtil.info(LINE);
        LoggerUtil.info("RESULT: " + resultLabel + summary);
        LoggerUtil.info(LINE);
        LoggerUtil.info("");
    }

    /**
     * Writes a clean, email-friendly HTML summary table to
     * {@code reports/emailable-report.html} at the end of the run:
     *  - Left column : Scenario name
     *  - Middle      : Feature file path (project-relative)
     *  - Right       : PASSED / FAILED / SKIPPED badge
     */
    private void onRunFinished(TestRunFinished event) {
        try {
            Path out = Paths.get("reports", "emailable-report.html");
            Files.createDirectories(out.getParent());

            int pass = 0, fail = 0, skip = 0;
            for (ScenarioRow r : scenarioRows) {
                if ("PASSED".equals(r.status)) pass++;
                else if ("FAILED".equals(r.status)) fail++;
                else if ("SKIPPED".equals(r.status)) skip++;
            }
            int total = scenarioRows.size();
            String generatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            StringBuilder html = new StringBuilder(8192);
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
                .append("<title>AgenticAI - Emailable Test Report</title>")
                .append("<style>")
                .append("body{font-family:Segoe UI,Arial,sans-serif;background:#f4f6f8;color:#222;margin:0;padding:24px;}")
                .append("h1{margin:0 0 4px;font-size:22px;}")
                .append(".sub{color:#666;margin-bottom:18px;font-size:13px;}")
                .append(".kpis{display:flex;gap:12px;margin-bottom:20px;flex-wrap:wrap;}")
                .append(".kpi{background:#fff;border:1px solid #e2e6ea;border-radius:8px;padding:12px 18px;min-width:110px;box-shadow:0 1px 2px rgba(0,0,0,.04);}")
                .append(".kpi .n{font-size:24px;font-weight:600;}")
                .append(".kpi .l{font-size:12px;color:#666;text-transform:uppercase;letter-spacing:.5px;}")
                .append(".kpi.pass .n{color:#1b9e4b;}.kpi.fail .n{color:#d62828;}.kpi.skip .n{color:#e08e0b;}.kpi.tot .n{color:#1f4e91;}")
                .append("table{width:100%;border-collapse:collapse;background:#fff;border:1px solid #e2e6ea;border-radius:8px;overflow:hidden;box-shadow:0 1px 2px rgba(0,0,0,.04);}")
                .append("th,td{padding:10px 14px;text-align:left;border-bottom:1px solid #eef0f2;font-size:13px;vertical-align:top;}")
                .append("th{background:#1f4e91;color:#fff;font-weight:600;text-transform:uppercase;letter-spacing:.5px;font-size:11px;}")
                .append("tr:last-child td{border-bottom:none;}")
                .append("tr:hover td{background:#f8fafc;}")
                .append(".scenario{font-weight:600;color:#222;}")
                .append(".path{font-family:Consolas,Menlo,monospace;color:#555;font-size:12px;}")
                .append(".dur{color:#666;font-variant-numeric:tabular-nums;}")
                .append(".badge{display:inline-block;padding:4px 10px;border-radius:12px;font-weight:600;font-size:11px;letter-spacing:.5px;}")
                .append(".badge.pass{background:#d8f3dc;color:#1b6b35;}")
                .append(".badge.fail{background:#fdd9d7;color:#a4161a;}")
                .append(".badge.skip{background:#fff3cd;color:#7a5800;}")
                .append(".badge.other{background:#e2e3e5;color:#495057;}")
                .append("</style></head><body>")
                .append("<h1>AgenticAI - BDD Test Run Summary</h1>")
                .append("<div class='sub'>Generated ").append(generatedAt)
                .append(" - <a href='ExtentReport_latest.html'>Open detailed Extent report</a></div>")
                .append("<div class='kpis'>")
                .append("<div class='kpi tot'><div class='n'>").append(total).append("</div><div class='l'>Total</div></div>")
                .append("<div class='kpi pass'><div class='n'>").append(pass).append("</div><div class='l'>Passed</div></div>")
                .append("<div class='kpi fail'><div class='n'>").append(fail).append("</div><div class='l'>Failed</div></div>")
                .append("<div class='kpi skip'><div class='n'>").append(skip).append("</div><div class='l'>Skipped</div></div>")
                .append("</div>")
                .append("<table><thead><tr>")
                .append("<th style='width:40%'>Scenario</th>")
                .append("<th style='width:40%'>Feature File (location in project)</th>")
                .append("<th style='width:10%'>Duration</th>")
                .append("<th style='width:10%'>Status</th>")
                .append("</tr></thead><tbody>");

            synchronized (scenarioRows) {
                for (ScenarioRow r : scenarioRows) {
                    String badgeClass;
                    switch (r.status) {
                        case "PASSED":  badgeClass = "pass"; break;
                        case "FAILED":  badgeClass = "fail"; break;
                        case "SKIPPED": badgeClass = "skip"; break;
                        default:        badgeClass = "other";
                    }
                    html.append("<tr>")
                        .append("<td class='scenario'>").append(escape(r.name)).append("</td>")
                        .append("<td class='path'>").append(escape(r.featurePath)).append("</td>")
                        .append("<td class='dur'>").append(r.durationMs).append(" ms</td>")
                        .append("<td><span class='badge ").append(badgeClass).append("'>")
                        .append(r.status).append("</span></td>")
                        .append("</tr>");
                }
            }

            html.append("</tbody></table></body></html>");
            Files.writeString(out, html.toString());
            LoggerUtil.info("Emailable summary report written: " + out.toAbsolutePath());
        } catch (IOException e) {
            LoggerUtil.warn("Could not write emailable-report.html: " + e.getMessage());
        }
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
