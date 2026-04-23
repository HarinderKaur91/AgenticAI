package com.agenticAI.autonomousFramework.Config;

import com.agenticAI.autonomousFramework.Enums.BrowserType;
import com.agenticAI.autonomousFramework.Enums.Environment;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Type-safe accessor over Typesafe Config (HOCON).
 *
 * Resolution order (highest precedence wins):
 *   1. JVM system properties        (-Dapp.baseUrl=...)
 *   2. Environment variables        (APP_BASEURL=...)
 *   3. {env}.conf                   (e.g. qa.conf)
 *   4. default.conf                 (shared baseline)
 *
 * Active environment is chosen via -Denv=qa (defaults to dev).
 */
public final class AppConfig {

    private static volatile AppConfig instance;

    private final Environment environment;
    private final Config config;

    private AppConfig() {
        this.environment = Environment.current();
        Config envConfig = ConfigFactory.parseResources("config/" + environment.configFile());
        Config defaults = ConfigFactory.parseResources("config/default.conf");
        this.config = ConfigFactory.systemProperties()
                .withFallback(ConfigFactory.systemEnvironment())
                .withFallback(envConfig)
                .withFallback(defaults)
                .resolve();
    }

    public static AppConfig get() {
        AppConfig local = instance;
        if (local == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
                local = instance;
            }
        }
        return local;
    }

    public Environment environment() { return environment; }

    public String baseUrl() { return config.getString("app.baseUrl"); }
    public String apiBaseUrl() { return config.getString("app.apiBaseUrl"); }

    public BrowserType browser() { return BrowserType.fromString(config.getString("app.browser")); }
    public boolean headless() { return config.getBoolean("app.headless"); }
    public int slowMoMs() { return config.getInt("app.slowMoMs"); }

    public int viewportWidth() { return config.getInt("app.viewport.width"); }
    public int viewportHeight() { return config.getInt("app.viewport.height"); }

    public int defaultTimeoutMs() { return config.getInt("app.timeouts.default"); }
    public int navigationTimeoutMs() { return config.getInt("app.timeouts.navigation"); }
    public int expectTimeoutMs() { return config.getInt("app.timeouts.expect"); }

    public boolean screenshotOnFailure() { return config.getBoolean("app.capture.screenshotOnFailure"); }
    public boolean traceOnFailure() { return config.getBoolean("app.capture.traceOnFailure"); }
    public boolean videoOnFailure() { return config.getBoolean("app.capture.videoOnFailure"); }
    public boolean screenshotEveryStep() { return config.getBoolean("app.capture.screenshotEveryStep"); }

    public String defaultSearchTerm() { return config.getString("app.testData.defaultSearchTerm"); }
    public String contactEmail() { return config.getString("app.testData.contactEmail"); }

    public String username() { return optionalString("app.credentials.username"); }
    public String password() { return optionalString("app.credentials.password"); }

    public boolean jiraEnabled() { return config.getBoolean("app.jira.enabled"); }
    public String jiraBaseUrl() { return config.getString("app.jira.baseUrl"); }
    public String jiraProjectKey() { return config.getString("app.jira.projectKey"); }
    public String jiraApiToken() { return optionalString("app.jira.apiToken"); }
    public String jiraUserEmail() { return optionalString("app.jira.userEmail"); }

    public boolean zephyrEnabled() { return config.getBoolean("app.zephyr.enabled"); }
    public String zephyrFlavour() { return config.getString("app.zephyr.flavour"); }
    public String zephyrApiToken() { return optionalString("app.zephyr.apiToken"); }

    private String optionalString(String path) {
        return config.hasPath(path) ? config.getString(path) : "";
    }
}
