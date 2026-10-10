Feature: Log in to the site

  @login
  Scenario Outline: Successful log in with valid credentials
    Given I am on the login page
    # When I click "SignUp/Login" button
    When I enter valid "<username>" and "<password>"
    Examples:
      | username       | password     |
      | standard_user  | secret_sauce |
