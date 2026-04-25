# Jira Project Context — AgenticAI Autonomous Framework

> This file is the **single source of truth** for Jira context the AI
> agent uses when analysing failures, raising issues, and proposing fixes.
> Update it whenever a story or epic changes.
> Live queries (when configured) come from the Atlassian MCP server in
> `.vscode/mcp.json`; this file is the offline fallback.

## Project

| Field        | Value                                               |
|--------------|-----------------------------------------------------|
| Project name | AgenticAI Autonomous Framework                      |
| Project key  | `SCRUM`                                             |
| Jira URL     | https://kaurharinder91.atlassian.net                |
| Board URL    | https://kaurharinder91.atlassian.net/jira/software/projects/SCRUM/boards/1 |
| Board type   | Scrum                                               |
| Workflow     | Backlog → To Do → In Progress → In Review → Done    |

## Epics

| Epic key   | Title                                  |
|------------|----------------------------------------|
| SCRUM-100  | Self-healing Playwright test framework |
| SCRUM-101  | Product catalog & search               |
| SCRUM-102  | Cart & checkout                        |
| SCRUM-103  | Account / authentication               |
| SCRUM-104  | Site engagement (contact, subscribe)   |

## Stories

| Story key  | Epic       | Title                                    | Acceptance Criteria (summary) |
|------------|------------|------------------------------------------|--------------------------------|
| SCRUM-1    | SCRUM-101  | Home page is reachable                   | Home loads, key nav links visible |
| SCRUM-2    | SCRUM-101  | Catalog browsing & search                | Catalog renders, search returns results, URL is `/products` |
| SCRUM-3    | SCRUM-102  | Add product to cart                      | Cart count increments, item appears in cart |
| SCRUM-4    | SCRUM-102  | Cart shows correct items & quantities    | Names + quantities match what was added |
| SCRUM-5    | SCRUM-103  | New user registration end-to-end         | Account created, deletable post-signup |
| SCRUM-6    | SCRUM-103  | Login rejects invalid credentials        | Error message shown, no session granted |
| SCRUM-7    | SCRUM-104  | Submit Contact Us form with attachment   | Success banner displayed |
| SCRUM-8    | SCRUM-104  | Subscribe to newsletter                  | Subscription success message displayed |
| SCRUM-9    | SCRUM-100  | Cross-browser smoke (chromium/firefox/webkit) | Catalog flow passes on all 3 engines |

## Conventions

- Every test method **must** carry `@JiraTestMeta(jira="SCRUM-N", zephyr="SCRUM-TN", ...)`.
- Every Cucumber scenario **must** carry `@JIRA-SCRUM-N @ZEPHYR-SCRUM-TN` tags.
- When the agent files a new bug, prefix the title with `[Auto] ` and label it `auto-raised`.
- Test catalog lives in [docs/zephyr/test-cases.md](../zephyr/test-cases.md).
- A Zephyr-importable CSV is generated at [docs/zephyr/zephyr-import.csv](../zephyr/zephyr-import.csv).

