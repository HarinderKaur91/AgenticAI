# language: en
# Linked to JIRA story SCRUM-3 / Zephyr test case SCRUM-T11
# See docs/bdd/business-flow.md for the full business flow.

@bdd @smoke @JIRA-SCRUM-3
Feature: Product detail page and add to cart
  As a shopper
  I want to view product details and add items to my cart
  So that I can purchase products I am interested in

  Background:
    Given the user is on the home page
    When the user navigates to the products catalog

  @ZEPHYR-SCRUM-T11
  Scenario: User can view product details from catalog
    When the user clicks on the first product card
    Then the product detail page should be displayed
    And the product name should be visible
    And the product price should be visible
    And the quantity selector should be available

  @ZEPHYR-SCRUM-T11 @cart
  Scenario Outline: User can add product to cart with quantity
    When the user clicks on the first product card
    And the user sets the quantity to <quantity>
    And the user clicks the "Add to Cart" button
    Then a cart confirmation message should appear
    And the cart count in the navigation should increment

    Examples:
      | quantity |
      | 1        |
      | 2        |
      | 3        |
