@smoke @navigation
Feature: ApiDemos home screen navigation

  The home screen lists every top-level sample category. Opening one shows the
  demos it contains, and Back returns to the top of the tree.

  Background:
    Given the ApiDemos app is open on the home screen

  @smoke
  Scenario: The home screen lists every top-level category
    Then the home screen shows the categories:
      | Accessibility |
      | Animation     |
      | App           |
      | Content       |
      | Graphics      |
      | Media         |
      | NFC           |
      | OS            |
      | Preference    |
      | Text          |
      | Views         |

  Scenario: Opening the Preference category lists its demos
    When I open the "Preference" category
    Then the list shows "1. Preferences from XML"
    And the list shows "3. Preference dependencies"

  Scenario: Going back from a category returns to the home screen
    When I open the "Preference" category
    And I navigate back
    Then the list shows "Views"
