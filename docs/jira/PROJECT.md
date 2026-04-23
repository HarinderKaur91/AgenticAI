# Jira Project Context — AgenticAI Autonomous Framework

> This file is the **single source of truth** for Jira context the AI
> agent uses when analysing failures, raising issues, and proposing fixes.
> Update it whenever a story or epic changes.
> Live queries (when configured) come from the Atlassian MCP server in
> `.vscode/mcp.json`; this file is the offline fallback.

## Project

| Field        | Value                                  |
|--------------|----------------------------------------|
| Project name | AgenticAI Autonomous Framework         |
| Project key  | `AAF`                                  |
| Jira URL     | https://your-org.atlassian.net (TODO)  |
| Board type   | Scrum                                  |
| Workflow     | Backlog → To Do → In Progress → In Review → Done |

> Replace the URL above and set `app.jira.baseUrl` in
> `src/test/resources/config/default.conf` once the real Jira project exists.

## Epics

| Epic key | Title                                     |
|----------|-------------------------------------------|
| AAF-1    | Self-healing Playwright test framework    |
| AAF-2    | Product catalog automation                |
| AAF-3    | Cart & checkout automation                |
| AAF-4    | Account / login automation                |

## Stories (TDD)

| Story key | Epic    | Title                                     | Acceptance Criteria (summary) |
|-----------|---------|-------------------------------------------|--------------------------------|
| AAF-10    | AAF-2   | Home page is reachable                    | Home page loads, key nav links visible |
| AAF-11    | AAF-2   | Products catalog displays items           | Catalog page shows products, count > 0 |
| AAF-12    | AAF-3   | User can add a product to the cart        | Cart count increments, item appears in cart |
| AAF-13    | AAF-2   | User can search the catalog               | Search returns results matching the term |
| AAF-14    | AAF-4   | User can submit Contact Us form           | Success message displayed |

## Stories (BDD)

| Story key | Epic    | Title                                     | Feature file |
|-----------|---------|-------------------------------------------|--------------|
| AAF-101   | AAF-2   | Product catalog browsing                  | `product_catalog.feature` |

## Conventions

- Every test method **must** carry `@JiraTestMeta(jira="AAF-NN", zephyr="AAF-TNN", ...)`.
- Every Cucumber scenario **must** carry `@JIRA-AAF-NN @ZEPHYR-AAF-TNN` tags.
- When the agent files a new bug, prefix the title with `[Auto] ` and label it `auto-raised`.
