# Zephyr Test Case Catalog

> Curated set of 10 test cases — chosen so that **every framework
> capability** (config, soft-assert, logging, reporting, traces,
> cross-browser, BDD, Page Objects) is exercised at least once.
>
> **Companion CSV** for one-click Zephyr Scale import:
> [`docs/zephyr/zephyr-import.csv`](zephyr-import.csv).
>
> Layered with [Jira project context](../jira/PROJECT.md). Project key: `SCRUM`.

## Catalog

| Zephyr key   | Linked Jira | Style | Title                                          | Test class / scenario                                          |
|--------------|-------------|-------|------------------------------------------------|----------------------------------------------------------------|
| SCRUM-T1     | SCRUM-1     | TDD   | Home page loads (smoke)                        | `PlaywrightTests#verifyHomePageLoads`                          |
| SCRUM-T2     | SCRUM-2     | BDD   | Catalog displays + search returns results      | `product_catalog.feature` (both scenarios)                     |
| SCRUM-T3     | SCRUM-2     | TDD   | Search "Top" returns matching products         | `PlaywrightTests#verifySearchProduct`                          |
| SCRUM-T4     | SCRUM-3     | TDD   | Add single product to cart                     | `PlaywrightTests#verifyAddSingleProductToCart`                 |
| SCRUM-T5     | SCRUM-4     | TDD   | Cart shows correct item names + quantity       | `ComplexPlaywrightTests` (cart verification)                   |
| SCRUM-T11    | SCRUM-3     | BDD   | Product detail page and add to cart            | `product_detail.feature` (both scenarios)                      |
| SCRUM-T12    | SCRUM-4     | BDD   | Cart page and cart management                  | `cart_page.feature` (all scenarios)                            |
| SCRUM-T6     | SCRUM-5     | BDD   | New user registration end-to-end               | `user_registration.feature`                                    |
| SCRUM-T7     | SCRUM-6     | TDD   | Login with invalid password is rejected        | `AuthenticationTests#verifyLoginWithInvalidPasswordRejected`   |
| SCRUM-T8     | SCRUM-7     | BDD   | Submit Contact Us form with attachment         | `contact_us.feature`                                           |
| SCRUM-T9     | SCRUM-8     | BDD   | Subscribe to newsletter from home footer       | `subscriptions.feature`                                        |
| SCRUM-T10    | SCRUM-9     | TDD   | Cross-browser smoke (catalog renders)          | `CrossBrowserSmokeTests#verifyCatalogRendersOnConfiguredBrowser` (run with `-Dapp.browser=firefox` / `webkit`) |

## Severity legend

| Severity   | Meaning                                                  |
|------------|----------------------------------------------------------|
| BLOCKER    | Stops all testing; release-stopper.                      |
| CRITICAL   | Major feature broken; high-priority fix.                 |
| MAJOR      | Feature partially broken; default for most cases.        |
| MINOR      | Cosmetic / low impact.                                   |
| TRIVIAL    | Nit / typo.                                              |

## How the agent uses this

When a test fails, the daily-test-fix workflow assigns the issue to the
Copilot coding agent. The agent reads:
1. The failing test name + the Jira/Zephyr keys from `@JiraTestMeta`.
2. The matching row in this file for context (linked story, severity).
3. [`docs/jira/PROJECT.md`](../jira/PROJECT.md) for story / acceptance criteria.
4. [`docs/bdd/business-flow.md`](../bdd/business-flow.md) if the failure is BDD.

## Importing into Zephyr Scale

1. Open Jira project `SCRUM` → **Tests** → **Import Test Cases** → **CSV**.
2. Choose [`docs/zephyr/zephyr-import.csv`](zephyr-import.csv).
3. Map the columns when prompted (the CSV uses Zephyr Scale's standard
   field names: `Name`, `Objective`, `Precondition`, `Test Script (Step-by-Step) - Step`,
   `Test Script (Step-by-Step) - Test Data`, `Test Script (Step-by-Step) - Expected Result`,
   `Labels`, `Priority`, `Issue Links`).
4. Run the import. Zephyr will assign keys `SCRUM-T1`..`SCRUM-T10`
   (or your tenant's next free numbers — update this file if they differ).

