package com.agenticAI.autonomousFramework.Enums;

/**
 * Supported execution environments.
 * Active env is selected via -Denv=qa (default: dev).
 */
public enum Environment {
    DEV,
    QA,
    PROD;

    public static Environment current() {
        String value = System.getProperty("env",
                System.getenv().getOrDefault("ENV", "dev"));
        return Environment.valueOf(value.trim().toUpperCase());
    }

    public String configFile() {
        return name().toLowerCase() + ".conf";
    }
}
