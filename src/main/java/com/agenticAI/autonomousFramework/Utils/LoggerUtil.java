package com.agenticAI.autonomousFramework.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Thin facade over SLF4J that also manages MDC keys used across the
 * framework. Every test gets a fresh correlationId so log lines,
 * Playwright traces and Extent report entries can be tied together.
 */
public class LoggerUtil {
    private static final Logger logger = LoggerFactory.getLogger("AUTO_LOG");

    public static final String MDC_CORRELATION_ID = "correlationId";
    public static final String MDC_TEST_NAME      = "testName";
    public static final String MDC_ENV            = "env";
    public static final String MDC_BROWSER        = "browser";

    public static String startTestContext(String testName, String env, String browser) {
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(MDC_CORRELATION_ID, correlationId);
        MDC.put(MDC_TEST_NAME, testName);
        MDC.put(MDC_ENV, env);
        MDC.put(MDC_BROWSER, browser);
        return correlationId;
    }

    public static void clearTestContext() {
        MDC.remove(MDC_CORRELATION_ID);
        MDC.remove(MDC_TEST_NAME);
        MDC.remove(MDC_ENV);
        MDC.remove(MDC_BROWSER);
    }

    public static String correlationId() {
        return MDC.get(MDC_CORRELATION_ID);
    }

    public static void info(String message)            { logger.info(message); }
    public static void debug(String message)           { logger.debug(message); }
    public static void warn(String message)            { logger.warn(message); }
    public static void error(String message)           { logger.error(message); }
    public static void error(String message, Throwable t) { logger.error(message, t); }
    public static void fatal(String message)           { logger.error("FATAL: " + message); }
}

