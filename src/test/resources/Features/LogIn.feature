Feature: Log in to the site

  @login1
  Scenario Outline: Successful log in with valid credentials
    Given I am on the login page
    When I click "SignUp/Login" button
    And I enter valid "<email>" and "<password>"
    Examples:
      | email               | password    |
      | student123@job.com  | 12345678    |
