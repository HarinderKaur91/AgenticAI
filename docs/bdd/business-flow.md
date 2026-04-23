# BDD Business Flow

> Plain-English description of the user journeys covered by the
> `.feature` files. The AI agent reads this file to understand
> intent before fixing failing scenarios.

## Persona

**Sam — the casual shopper.** Sam visits the AutomationExercise
storefront to browse, search, and shortlist clothing items. Sam is
not logged in for the catalog flow.

## Flow 1 — Browse the catalog (`product_catalog.feature`)

| Step | Sam's intent                              | System response                                  |
|------|-------------------------------------------|--------------------------------------------------|
| 1    | Lands on the home page                    | Home renders, top nav is visible                 |
| 2    | Clicks **Products** in the top nav        | Navigates to `/products`, catalog grid loads     |
| 3    | Sees the full catalog                     | At least one product card is shown               |
| 4    | Types a term in the search box and submits| Catalog filters to matching products             |
| 5    | Confirms the "Searched Products" heading appears | Visual confirmation the filter applied   |

### Acceptance criteria (mirrors Jira AAF-101)
- Home page is reachable without authentication.
- Products page must list **at least one** product card.
- Search results section must render the heading **Searched Products**.
- URL must contain `/products` after navigation.

### Out of scope for this flow
- Adding to cart (covered by Flow 2 once added).
- Authentication (covered by Flow 3 once added).

## Adding new flows

1. Add a new `## Flow N — Title` section here.
2. Create the matching `.feature` file under `src/test/resources/features/`.
3. Tag the scenarios with `@JIRA-AAF-NNN @ZEPHYR-AAF-TNNN`.
4. Add the corresponding row to `docs/jira/PROJECT.md` (Stories — BDD).
5. Add step definitions under `com.agenticAI.autonomousFramework.bdd.steps`.
