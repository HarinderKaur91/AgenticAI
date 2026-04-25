# language: en
# Linked to JIRA story SCRUM-7 / Zephyr test case SCRUM-T8

@bdd @regression @JIRA-SCRUM-7
Feature: Contact Us form submission
  As a visitor
  I want to send a message to the site owner
  So that I can get support without an account

  @ZEPHYR-SCRUM-T8
  Scenario: Submitting the Contact Us form with a valid attachment succeeds
    Given the user is on the home page
    When the user opens the Contact Us page
    And the user submits the contact form with a sample attachment
    Then a success confirmation message should be displayed
