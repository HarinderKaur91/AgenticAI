# Zephyr Test Case Catalog (TDD)

> Mirrors what should exist in Zephyr Scale under project `AAF`.
> Keep in sync with TestNG `@Test` methods. The nightly workflow
> `.github/workflows/zephyr-sync.yml` (when enabled) refreshes this
> file from the Zephyr REST API.

## Test Cases

| Zephyr key | Linked Jira | Title                                          | Test class / method |
|------------|-------------|------------------------------------------------|---------------------|
| AAF-T1     | AAF-10      | Verify home page loads                         | `PlaywrightTests#verifyHomePageLoads` |
| AAF-T2     | AAF-10      | Verify navigation to test cases page           | `PlaywrightTests#verifyTestCasesPageNavigation` |
| AAF-T3     | AAF-11      | Verify products page is visible                | `PlaywrightTests#verifyProductsPageIsVisible` |
| AAF-T4     | AAF-13      | Verify search returns matching products        | `PlaywrightTests#verifySearchProduct` |
| AAF-T5     | AAF-11      | Verify product detail page renders             | `PlaywrightTests#verifyFirstProductDetailPage` |
| AAF-T6     | AAF-12      | Verify a single product can be added to cart   | `PlaywrightTests#verifyAddSingleProductToCart` |

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
3. `docs/jira/PROJECT.md` for story/AC details.
4. `docs/bdd/business-flow.md` if the failure is BDD.
