Feature: View products from Products page

  @ProductsPage
  Scenario Outline:
  TC_PRODUCTS_004 - Add Product to Cart
  TC_PRODUCTS_005 - Continue Shopping from Cart Modal
  TC_PRODUCTS_006 - Filter Products by Category
  TC_PRODUCTS_007 - Filter Products by Brand

    Given I am on the login page
    When I click "SignUp/Login" button
    And I enter valid "<email>" and "<password>"
    When I go to Products page
    And I check Product card details
    And I view a product detail
    #TC_PRODUCTS_004
    Then I add the product to cart
    When I go to cart page
#    Then I should see the product in the cart



    @TC_PRODUCTS_004
    Examples:
      | email           | password        |
      | buck@wild.com   | callofthewild   |