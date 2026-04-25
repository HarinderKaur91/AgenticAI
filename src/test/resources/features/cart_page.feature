# language: en
# Linked to JIRA story SCRUM-4 / Zephyr test case SCRUM-T12
# See docs/bdd/business-flow.md for the full business flow.

@bdd @regression @JIRA-SCRUM-4
Feature: Cart page and cart management
  As a shopper
  I want to view, modify, and manage items in my shopping cart
  So that I can verify my order before checkout

  Background:
    Given the user is on the home page
    When the user navigates to the products catalog
    And the user clicks on the first product card
    And the user sets the quantity to 1
    And the user clicks the "Add to Cart" button

  @ZEPHYR-SCRUM-T12
  Scenario: User can view cart with added items
    When the user navigates to the cart page
    Then the cart page should be displayed
    And the added product should be visible in the cart
    And the product name should match the original product
    And the quantity should be 1

  @ZEPHYR-SCRUM-T12 @cart
  Scenario: User can update product quantity in cart
    When the user navigates to the cart page
    And the user updates the product quantity to 3
    Then the cart should reflect the updated quantity
    And the cart total should be recalculated

  @ZEPHYR-SCRUM-T12 @cart
  Scenario: User can remove item from cart
    When the user navigates to the cart page
    And the user removes the product from the cart
    Then the product should be removed from the cart
    And the cart should be empty
    And an empty cart message should be displayed
