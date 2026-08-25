@preferences
Feature: Preference dependencies

  Preference / 3. Preference dependencies gates the "WiFi settings" preference
  behind the "WiFi" checkbox: until WiFi is ticked, WiFi settings cannot be
  opened.

  Background:
    Given the ApiDemos app is open on the home screen
    When I open the "Preference" category
    And I open the "3. Preference dependencies" demo

  @smoke
  Scenario: WiFi settings stays disabled until WiFi is ticked
    Then the "WiFi settings" preference is disabled
    When I tick the "WiFi" checkbox
    Then the "WiFi settings" preference is enabled

  Scenario: A value entered for WiFi settings is kept
    When I tick the "WiFi" checkbox
    And I open the "WiFi settings" preference
    And I enter "Home network" into the dialog and confirm
    And I open the "WiFi settings" preference
    Then the dialog input contains "Home network"
