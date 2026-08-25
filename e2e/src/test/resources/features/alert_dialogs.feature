@dialogs
Feature: Alert dialogs

  App / Alert Dialogs offers one row per dialog style. The two-button dialog is
  the simplest end-to-end check: open it, confirm both buttons, dismiss it.

  Background:
    Given the ApiDemos app is open on the home screen
    When I open the "App" category
    And I open the "Alert Dialogs" demo

  @smoke
  Scenario: The OK/Cancel dialog opens and can be dismissed
    When I tap the "OK Cancel dialog with a message" row
    Then a dialog is displayed
    And the dialog positive button reads "OK"
    And the dialog negative button reads "Cancel"
    When I dismiss the dialog with the negative button
    Then no dialog is displayed
