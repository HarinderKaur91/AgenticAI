package com.agenticAI.autonomousFramework.Annotations;

import com.agenticAI.autonomousFramework.Enums.TestSeverity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Links a TestNG test method to its Jira story and Zephyr test case.
 * Rendered as clickable badges in ExtentReports and consumed by the
 * AI agent (via copilot-instructions.md) for traceability.
 *
 * Example:
 * <pre>
 * &#64;JiraTestMeta(jira = "AAF-12", zephyr = "AAF-T45", severity = TestSeverity.MAJOR,
 *                story = "Add product to cart")
 * &#64;Test
 * public void verifyAddSingleProductToCart() { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface JiraTestMeta {
    String jira() default "";
    String zephyr() default "";
    String story() default "";
    TestSeverity severity() default TestSeverity.MAJOR;
}
