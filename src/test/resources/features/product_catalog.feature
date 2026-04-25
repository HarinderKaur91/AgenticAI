# language: en
# Linked to JIRA story SCRUM-2 / Zephyr test case SCRUM-T2
# See docs/bdd/business-flow.md for the full business flow.

@bdd @smoke @JIRA-SCRUM-2
Feature: Product catalog browsing
  As a shopper
  I want to browse and search the product catalog
  So that I can find items I am interested in

  Background:
    Given the user is on the home page

  @ZEPHYR-SCRUM-T2
  Scenario: Catalog displays with at least one product
    When the user navigates to the products catalog
    Then the products catalog should be displayed
    And the page URL should contain "/products"

  @ZEPHYR-SCRUM-T2 @search
  Scenario Outline: User can search the catalog
    When the user navigates to the products catalog
    And the user searches for "<term>"
    Then search results should reference "<term>"

    Examples:
      | term  |
      | Top   |
      | Dress |
