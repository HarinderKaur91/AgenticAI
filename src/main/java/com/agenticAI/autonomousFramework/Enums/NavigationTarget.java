package com.agenticAI.autonomousFramework.Enums;

/**
 * Site-wide navigation targets for AutomationExercise.
 * Centralising URL paths avoids hard-coding strings across Page Objects.
 */
public enum NavigationTarget {
    HOME("/"),
    PRODUCTS("/products"),
    LOGIN("/login"),
    SIGNUP("/login"),
    CART("/view_cart"),
    CONTACT_US("/contact_us"),
    TEST_CASES("/test_cases"),
    API_LIST("/api_list");

    private final String path;

    NavigationTarget(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}
