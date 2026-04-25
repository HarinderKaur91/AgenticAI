# language: en
# Linked to JIRA story SCRUM-5 / Zephyr test case SCRUM-T6
# Covers the full registration journey: signup -> account info -> login -> delete.

@bdd @regression @JIRA-SCRUM-5
Feature: User registration end-to-end
  As a new visitor
  I want to register, log in, and delete my account
  So that the account-management flow stays reliable

  @ZEPHYR-SCRUM-T6
  Scenario: A new user can register, log in, and delete their account
    Given the user is on the home page
    When the user opens the signup/login page
    And the user signs up with a new identity
    And the user fills in mandatory account information
    Then the account-created confirmation should appear
    When the user continues to the home page
    Then the user should be greeted by name in the navigation
    When the user deletes the account
    Then the account-deleted confirmation should appear
