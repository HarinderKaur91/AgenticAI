# BDD Business Flow

> Plain-English description of the user journeys covered by the
> `.feature` files. The AI agent reads this file to understand
> intent before fixing failing scenarios.
>
> **Target site:** https://automationexercise.com
> **Last updated:** 2026-04-25

---

## Table of Contents

- [Personas](#personas)
- [Coverage Matrix](#coverage-matrix)
- [Flow 1 — Browse the catalog](#flow-1--browse-the-catalog-product_catalogfeature)
- [Flow 2 — Register and delete an account](#flow-2--register-and-delete-an-account-user_registrationfeature)
- [Flow 3 — Submit Contact Us](#flow-3--submit-contact-us-contact_usfeature)
- [Flow 4 — Subscribe to the newsletter](#flow-4--subscribe-to-the-newsletter-subscriptionsfeature)
- [Flow 5 — View product details and add to cart](#flow-5--view-product-details-and-add-to-cart-product_detailfeature)
- [Flow 6 — View and manage cart](#flow-6--view-and-manage-cart-cart_pagefeature)
- [Adding new flows](#adding-new-flows)

---

## Personas

| Persona | Role | Authenticated? | Used in flows |
|---------|------|----------------|---------------|
| **Sam**   | Casual shopper — browses & searches the catalog          | No  | Flow 1 |
| **Riley** | New account holder — signs up, verifies, then deletes    | Yes | Flow 2 |
| **Jordan**| Support seeker — submits Contact Us with attachment      | No  | Flow 3 |
| **Pat**   | Newsletter subscriber — joins from the footer            | No  | Flow 4 |
| **Alex**  | Returning shopper — views product details & manages cart | No  | Flow 5, 6 |

---

## Coverage Matrix

| Flow | Feature File | Jira | Zephyr | Severity | Tag |
|------|--------------|------|--------|----------|-----|
| 1 | `product_catalog.feature` | SCRUM-2 | SCRUM-T2, SCRUM-T3 | HIGH     | `@smoke @regression` |
| 2 | `user_registration.feature` | SCRUM-5 | SCRUM-T6           | CRITICAL | `@smoke @regression` |
| 3 | `contact_us.feature`        | SCRUM-7 | SCRUM-T8           | MEDIUM   | `@regression`        |
| 4 | `subscriptions.feature`     | SCRUM-8 | SCRUM-T9           | LOW      | `@regression`        |
| 5 | `product_detail.feature`    | SCRUM-3 | SCRUM-T4           | HIGH     | `@smoke @regression` |
| 6 | `cart_page.feature`         | SCRUM-4 | SCRUM-T5           | CRITICAL | `@smoke @regression` |

---

## Flow 1 — Browse the catalog (`product_catalog.feature`)
Linked to **SCRUM-2** (Story) / **SCRUM-T2, SCRUM-T3** (Zephyr).

| Step | Sam's intent                              | System response                                  |
|------|-------------------------------------------|--------------------------------------------------|
| 1    | Lands on the home page                    | Home renders, top nav is visible                 |
| 2    | Clicks **Products** in the top nav        | Navigates to `/products`, catalog grid loads     |
| 3    | Sees the full catalog                     | At least one product card is shown               |
| 4    | Types a term in the search box and submits| Catalog filters to matching products             |
| 5    | Confirms the "Searched Products" heading appears | Visual confirmation the filter applied   |

### Acceptance criteria
- Home page is reachable without authentication.
- Products page lists **at least one** product card.
- Search results section renders the heading **Searched Products**.
- URL contains `/products` after navigation.

---

## Flow 2 — Register and delete an account (`user_registration.feature`)
Linked to **SCRUM-5** (Story) / **SCRUM-T6** (Zephyr).

| Step | Intent                                            | System response                                  |
|------|---------------------------------------------------|--------------------------------------------------|
| 1    | Opens **Signup / Login** from the top nav         | Login/Signup page renders                        |
| 2    | Submits a unique name + email under "New User Signup" | Enter Account Information page is shown      |
| 3    | Fills mandatory account information               | Form accepts the data                            |
| 4    | Submits the account creation form                 | **Account Created!** confirmation is displayed   |
| 5    | Continues to the home page                        | Top nav now shows **Logged in as &lt;name&gt;**  |
| 6    | Clicks **Delete Account**                         | **Account Deleted!** confirmation is displayed   |

### Acceptance criteria
- Email used for signup must be unique per scenario (`TestDataUtil.uniqueEmail()`).
- Post-signup, the user is greeted by name in the navigation.
- Account deletion succeeds without errors.

---

## Flow 3 — Submit Contact Us (`contact_us.feature`)
Linked to **SCRUM-7** (Story) / **SCRUM-T8** (Zephyr).

| Step | Intent                                            | System response                                  |
|------|---------------------------------------------------|--------------------------------------------------|
| 1    | Opens **Contact Us** from the top nav             | Contact Us page renders with **Get In Touch**    |
| 2    | Fills name / email / subject / message            | Form accepts the data                            |
| 3    | Attaches `src/test/resources/test-upload.txt`     | File input shows the attachment                  |
| 4    | Submits the form and accepts the JS confirm dialog| Page returns success banner                      |

### Acceptance criteria
- Submission must include a non-empty attachment.
- Success message **Success! Your details have been submitted successfully.** must be displayed.

---

## Flow 4 — Subscribe to the newsletter (`subscriptions.feature`)
Linked to **SCRUM-8** (Story) / **SCRUM-T9** (Zephyr).

| Step | Intent                                            | System response                                  |
|------|---------------------------------------------------|--------------------------------------------------|
| 1    | Scrolls to the footer subscription form           | Subscription email field is visible              |
| 2    | Enters a unique email and submits                 | Subscription is accepted                         |
| 3    | Confirms the success banner                       | **You have been successfully subscribed!** shown |

### Acceptance criteria
- The footer subscription input **#susbscribe_email** must be reachable on the home page.
- Success banner must contain **successfully subscribed**.

---

## Flow 5 — View product details and add to cart (`product_detail.feature`)
Linked to **SCRUM-3** (Story) / **SCRUM-T4** (Zephyr).

| Step | Alex's intent                             | System response                                  |
|------|-------------------------------------------|--------------------------------------------------|
| 1    | Navigates to the catalog                  | Products page loads with product cards          |
| 2    | Clicks on a product card                  | Product detail page loads with full information |
| 3    | Reviews product name, price, quantity     | All product details are visible                  |
| 4    | Enters desired quantity and clicks "Add to Cart" | Item is added to cart                        |
| 5    | Confirms cart count increments            | Cart badge in nav shows updated count            |

### Acceptance criteria
- Product detail page displays product name, price, description, and quantity selector.
- Add to cart button adds the correct quantity to the cart.
- Cart count in the navigation header increments correctly.
- Product detail page is reachable from any catalog search result.

---

## Flow 6 — View and manage cart (`cart_page.feature`)
Linked to **SCRUM-4** (Story) / **SCRUM-T5** (Zephyr).

| Step | Alex's intent                             | System response                                  |
|------|-------------------------------------------|--------------------------------------------------|
| 1    | Clicks the cart icon in the top nav       | Cart page loads with items list                 |
| 2    | Verifies product names and quantities     | All added items appear with correct details     |
| 3    | Changes the quantity of an item           | Updated quantity is reflected in cart total     |
| 4    | Removes an item from cart                 | Item is deleted, cart updates                    |
| 5    | Confirms cart total price calculation     | Total reflects current items and quantities      |

### Acceptance criteria
- Cart page displays all added items with product name, price, and quantity.
- Quantity updates are reflected in the cart total and UI immediately.
- Remove button successfully deletes items from the cart.
- Cart persists across page navigation (for the session).
- Empty cart message is shown when no items remain.

---

## Adding new flows

1. Add a new `## Flow N — Title` section here.
2. Create the matching `.feature` file under `src/test/resources/features/`.
3. Tag the scenarios with `@JIRA-SCRUM-NN @ZEPHYR-SCRUM-TNN`.
4. Add the corresponding row to `docs/jira/PROJECT.md` (Stories) and `docs/zephyr/test-cases.md`.
5. Add step definitions under `com.agenticAI.autonomousFramework.bdd.steps` and reuse Page Objects — never duplicate selectors.
