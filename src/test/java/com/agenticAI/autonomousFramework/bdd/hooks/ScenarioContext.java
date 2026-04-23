package com.agenticAI.autonomousFramework.bdd.hooks;

import com.agenticAI.autonomousFramework.Config.AppConfig;
import com.microsoft.playwright.Page;

/**
 * Per-scenario thread-local storage for the Playwright Page and config.
 * Step definitions access these instead of holding their own state.
 */
public final class ScenarioContext {

    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
    private static final ThreadLocal<AppConfig> CFG = new ThreadLocal<>();

    private ScenarioContext() {}

    public static void set(Page page, AppConfig cfg) {
        PAGE.set(page);
        CFG.set(cfg);
    }

    public static Page page() {
        Page p = PAGE.get();
        if (p == null) throw new IllegalStateException("ScenarioContext not initialised - missing @Before hook?");
        return p;
    }

    public static AppConfig cfg() {
        AppConfig c = CFG.get();
        if (c == null) throw new IllegalStateException("ScenarioContext not initialised - missing @Before hook?");
        return c;
    }

    public static void clear() {
        PAGE.remove();
        CFG.remove();
    }
}
