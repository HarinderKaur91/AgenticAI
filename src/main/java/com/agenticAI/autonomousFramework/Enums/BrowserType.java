package com.agenticAI.autonomousFramework.Enums;

/**
 * Browsers supported by Playwright. Maps directly to Playwright's BrowserType.
 */
public enum BrowserType {
    CHROMIUM,
    FIREFOX,
    WEBKIT;

    public static BrowserType fromString(String value) {
        if (value == null || value.isBlank()) {
            return CHROMIUM;
        }
        return BrowserType.valueOf(value.trim().toUpperCase());
    }
}
