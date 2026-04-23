package com.agenticAI.autonomousFramework.Asserts;

import com.agenticAI.autonomousFramework.Utils.LoggerUtil;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom soft-assertion utility built on top of Hamcrest matchers.
 *
 * <p>Each assertion is recorded; failures are accumulated rather than
 * thrown immediately. Call {@link #assertAll()} (typically from
 * {@code @AfterMethod}) to fail the test if anything was collected.
 *
 * <p>Every assertion is also mirrored to the active ExtentReports
 * test node and to the structured log so reports stay in sync.
 *
 * <pre>
 * SoftAssert softly = new SoftAssert();
 * softly.assertThat("Cart count", cart.count(), is(equalTo(3)));
 * softly.assertThat("Title",     page.title(), containsString("Cart"));
 * softly.assertAll();
 * </pre>
 */
public class SoftAssert {

    private final List<String> failures = new ArrayList<>();

    public <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
        ExtentTest node = com.agenticAI.autonomousFramework.Utils.ExtentReportManager.getTest();
        try {
            org.hamcrest.MatcherAssert.assertThat(reason, actual, matcher);
            String description = describe(reason, actual, matcher, true);
            LoggerUtil.info("[SOFT-PASS] " + description);
            if (node != null) {
                node.log(Status.PASS, description);
            }
        } catch (AssertionError error) {
            String description = describe(reason, actual, matcher, false);
            failures.add(description);
            LoggerUtil.warn("[SOFT-FAIL] " + description);
            if (node != null) {
                node.log(Status.WARNING, description);
            }
        }
    }

    public boolean hasFailures() {
        return !failures.isEmpty();
    }

    public int failureCount() {
        return failures.size();
    }

    public void assertAll() {
        if (failures.isEmpty()) {
            return;
        }
        StringBuilder builder = new StringBuilder("Soft assertions failed (")
                .append(failures.size()).append("):");
        for (int i = 0; i < failures.size(); i++) {
            builder.append("\n  ").append(i + 1).append(". ").append(failures.get(i));
        }
        ExtentTest node = com.agenticAI.autonomousFramework.Utils.ExtentReportManager.getTest();
        if (node != null) {
            node.log(Status.FAIL, builder.toString());
        }
        failures.clear();
        throw new AssertionError(builder.toString());
    }

    private static <T> String describe(String reason, T actual, Matcher<? super T> matcher, boolean passed) {
        StringDescription description = new StringDescription();
        description.appendText(passed ? "PASS: " : "FAIL: ")
                .appendText(reason == null ? "(no reason)" : reason)
                .appendText(" -- expected ");
        matcher.describeTo(description);
        description.appendText(", actual ");
        matcher.describeMismatch(actual, description);
        return description.toString();
    }
}
