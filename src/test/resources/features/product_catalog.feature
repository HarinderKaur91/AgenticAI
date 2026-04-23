# language: en
# Linked to JIRA story AAF-101 / Zephyr test cases AAF-T201..T203
# See docs/bdd/business-flow.md for the full business flow.

@bdd @smoke @JIRA-AAF-101
Feature: Product catalog browsing
  As a shopper
  I want to browse and search the product catalog
  So that I can find items I am interested in

  Background:
    Given the user is on the home page

  @ZEPHYR-AAF-T201
  Scenario: Catalog is displayed when navigating from the home page
    When the user navigates to the products catalog
    Then the products catalog should be displayed
    And the page URL should contain "/products"

  @ZEPHYR-AAF-T202
  Scenario Outline: User can search the catalog
    When the user navigates to the products catalog
    And the user searches for "<term>"
    Then search results should reference "<term>"

    Examples:
      | term  |
      | Top   |
      | Dress |
