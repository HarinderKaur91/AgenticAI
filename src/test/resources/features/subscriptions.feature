# language: en
# Linked to JIRA story SCRUM-8 / Zephyr test case SCRUM-T9

@bdd @smoke @JIRA-SCRUM-8
Feature: Newsletter subscriptions
  As a visitor
  I want to subscribe to the newsletter
  So that I receive product updates

  @ZEPHYR-SCRUM-T9
  Scenario: Subscribing from the home page footer succeeds
    Given the user is on the home page
    When the user subscribes with a unique email from the footer
    Then a subscription success message should be displayed
