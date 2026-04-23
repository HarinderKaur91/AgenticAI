# GitHub Copilot — repo-wide instructions

You are working in the **AgenticAI Autonomous Framework**: a Java 21
+ Maven + Microsoft Playwright + TestNG + Cucumber test suite that
exercises https://automationexercise.com.

## Stack at a glance

| Concern              | Tool                                                |
|----------------------|-----------------------------------------------------|
| Language / build     | Java 21, Maven                                      |
| Browser automation   | Microsoft Playwright for Java 1.58.x                |
| TDD test runner      | TestNG 7.x                                          |
| BDD test runner      | Cucumber-JVM 7.x via TestNG                         |
| Assertions           | Custom `SoftAssert` (Hamcrest-backed) + TestNG `Assert` |
| Config               | Typesafe Config (HOCON) — `src/test/resources/config/` |
| Logging              | SLF4J + Log4j2 (text + JSON, MDC-aware)             |
| Reporting            | ExtentReports (Spark) + Cucumber HTML/JSON          |
| MCP servers          | `@playwright/mcp` (local), Atlassian remote (opt-in)|
| CI                   | GitHub Actions — `.github/workflows/`               |

## Source layout

```
src/main/java/com/agenticAI/autonomousFramework/
  Annotations/   -> @JiraTestMeta linking tests to Jira/Zephyr
  Asserts/       -> SoftAssert (Hamcrest)
  Base/          -> BaseTest (Playwright lifecycle)
  Config/        -> AppConfig (HOCON accessor)
  Enums/         -> Environment, BrowserType, NavigationTarget, UserRole, TestSeverity
  Pages/         -> Page Objects
  Utils/         -> LoggerUtil, ExtentReportManager
src/test/java/com/agenticAI/autonomousFramework/
  Java/          -> TestNG (TDD) tests
  Utils/         -> TestListener, TestDataUtil
  bdd/
    runners/     -> CucumberTestNGRunner
    hooks/       -> CucumberHooks, ScenarioContext
    steps/       -> Cucumber step definitions
src/test/resources/
  config/        -> default/dev/qa/prod .conf (HOCON)
  features/      -> .feature files (Gherkin)
  log4j2.xml
docs/
  jira/PROJECT.md            -> Jira project + stories context (READ FIRST)
  zephyr/test-cases.md       -> Zephyr test catalog
  bdd/business-flow.md       -> BDD business-flow narrative
  SETUP.md                   -> developer setup
```

## Mandatory rules when modifying tests

1. **Never hard-code** URLs, browser names, timeouts, viewport sizes,
   credentials, or env-specific values. Read them from `AppConfig` and
   the relevant enum (`NavigationTarget`, `BrowserType`, `Environment`).
2. **Never commit secrets.** Credentials, Jira tokens, and Zephyr tokens
   come from environment variables (see `default.conf`).
3. **Every TestNG test** must declare `@JiraTestMeta(jira=..., zephyr=...,
   story=..., severity=...)`. Every Cucumber scenario must carry
   `@JIRA-AAF-NN @ZEPHYR-AAF-TNN` tags.
4. **Use `SoftAssert`** for multi-assertion verifications so the report
   collects all failures in one go. Reserve TestNG's hard `Assert` for
   preconditions that make the rest of the test pointless.
5. **Reuse existing Page Objects** — do not duplicate selectors in step
   definitions or test classes.
6. **Logging** — call `LoggerUtil.info/warn/error`; the framework already
   manages MDC (`testName`, `env`, `browser`, `correlationId`).
7. **Don't fabricate Jira/Zephyr keys.** If a key isn't in
   `docs/jira/PROJECT.md` or `docs/zephyr/test-cases.md`, ask before
   inventing one.

## When fixing failing tests (Copilot coding agent)

1. Read the failing test class + method.
2. Read `docs/jira/PROJECT.md` to understand the story / acceptance
   criteria the test is enforcing.
3. Read `docs/zephyr/test-cases.md` for the test's intent.
4. If the failure is BDD, read `docs/bdd/business-flow.md`.
5. **Do not weaken assertions** to make a test pass — find the actual
   defect in the Page Object, locator, or wait.
6. **Do not delete tests.** If a test is genuinely impossible to run,
   add `@Test(enabled = false)` with a comment citing the Jira issue.
7. When proposing a fix, attach the Playwright trace path from
   `reports/traces/` to the PR description.

## Running

```powershell
mvn test                              # full suite
mvn test -Denv=qa                     # qa.conf
mvn test -Dgroups=tdd                 # not yet wired -- use class filtering for now
mvn test -Dcucumber.filter.tags=@smoke # BDD smoke only
```

## Playwright MCP

`.vscode/mcp.json` registers `@playwright/mcp` so you can drive a real
browser when authoring or repairing tests. Use it to:
- Re-discover stable selectors when one breaks.
- Verify a fix by replaying the user journey.
- Capture an updated screenshot for the PR.
