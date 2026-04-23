# AGENTS.md

Repository-level guidance for autonomous coding agents
(GitHub Copilot coding agent, Claude Code, Codex, etc.).

## Mission

Maintain and extend the AgenticAI Autonomous Framework — a self-healing
Playwright/TestNG/Cucumber test suite for https://automationexercise.com —
without weakening test rigor.

## Where to look first

| Question                            | File                                        |
|-------------------------------------|---------------------------------------------|
| Build / run commands                | `docs/SETUP.md`                             |
| Stack, conventions, hard rules      | `.github/copilot-instructions.md`           |
| Jira project, stories, AC           | `docs/jira/PROJECT.md`                      |
| Zephyr test catalog (TDD)           | `docs/zephyr/test-cases.md`                 |
| BDD business-flow narrative         | `docs/bdd/business-flow.md`                 |
| Env config schema                   | `src/test/resources/config/default.conf`    |
| Playwright MCP / Atlassian MCP      | `.vscode/mcp.json`                          |

## Hard constraints

- **No hard-coded URLs / timeouts / browser names / credentials.**
  Use `AppConfig` and the `Enums` package.
- **No committed secrets.** Use env vars; see `docs/SETUP.md`.
- **Every test carries `@JiraTestMeta`** (TestNG) or
  `@JIRA-AAF-NN @ZEPHYR-AAF-TNN` tags (Cucumber).
- **Page Objects own selectors.** Step definitions and tests must not
  duplicate locators.
- **Soft assertions by default.** Use `com.agenticAI.autonomousFramework.Asserts.SoftAssert`.

## Build verification

```powershell
mvn -DskipTests test-compile          # must succeed before opening any PR
mvn test                              # full TDD + BDD suite
```

## Reporting expectations on every fix PR

- Updated ExtentReport screenshot.
- Path to the Playwright trace zip (under `reports/traces/`).
- Reference to the Jira/Zephyr key that the change covers.

## Out-of-scope changes

- Do not switch test frameworks (no JUnit migration).
- Do not replace ExtentReports with Allure unless explicitly asked.
- Do not target sites other than automationexercise.com.
