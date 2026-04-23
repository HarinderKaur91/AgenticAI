# Setup Guide

## Prerequisites
- Java 21
- Maven 3.9+
- Node.js 20+ (only needed for MCP servers)

## First run
```powershell
mvn -DskipTests test-compile          # compile only
mvn test                              # run full suite (TDD + BDD)
mvn test -Denv=qa                     # use qa.conf overrides
mvn test -Dcucumber.filter.tags=@smoke   # only BDD smoke scenarios
```

## Environment configuration

Configuration is layered (highest precedence first):

1. JVM system properties: `-Dapp.baseUrl=https://staging.example.com`
2. Environment variables: `APP_BASEURL=...`
3. `src/test/resources/config/{env}.conf` — env-specific overrides
4. `src/test/resources/config/default.conf` — shared baseline

Active env is selected via `-Denv=dev|qa|prod` (default: `dev`).

### Secrets
Secrets must come from environment variables, never from committed files:
```powershell
$env:AUTOMATION_USERNAME = "qa-user"
$env:AUTOMATION_PASSWORD = "***"
$env:JIRA_API_TOKEN      = "***"
$env:JIRA_USER_EMAIL     = "you@example.com"
$env:ZEPHYR_API_TOKEN    = "***"
```

## Enabling Jira MCP (optional)

1. Create your Jira project (key `AAF`).
2. Set `app.jira.enabled = true` in `default.conf`.
3. Update `app.jira.baseUrl` and `app.jira.projectKey`.
4. Open VS Code in this workspace; you'll be prompted for the
   Atlassian token the first time the agent calls the MCP server
   (see `.vscode/mcp.json`).

## Enabling nightly Zephyr sync (optional)

1. Create the GitHub repo secret `ZEPHYR_API_TOKEN`.
2. Optionally edit `.github/workflows/zephyr-sync.yml` schedule.
3. The workflow updates `docs/zephyr/test-cases.md` once enabled.

## Playwright MCP (local dev only)

`.vscode/mcp.json` registers `@playwright/mcp` so the AI agent can
drive a real browser to inspect AutomationExercise while authoring
or fixing tests. Nothing extra to install — `npx` fetches it on
first use.

## Reports & artifacts

| Artifact            | Location                                       |
|---------------------|------------------------------------------------|
| ExtentReports HTML  | `reports/ExtentReport_<timestamp>.html`        |
| Cucumber HTML       | `reports/cucumber/cucumber-report.html`        |
| Cucumber JSON       | `reports/cucumber/cucumber-report.json`        |
| Failure screenshots | `screenshots/`                                 |
| Playwright traces   | `reports/traces/*.zip` (open with `npx playwright show-trace`) |
| Playwright videos   | `reports/videos/`                              |
| Plain-text logs     | `logs/test-execution.log`                      |
| Structured JSON logs| `logs/test-execution.json`                     |
