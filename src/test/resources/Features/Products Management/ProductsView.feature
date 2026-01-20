Feature: View products from Products page

  @ProductsPage
  Scenario Outline: TC_PRODUCTS_001 - Verify Products Page Loads Successfully
                    TC_PRODUCTS_002 - Verify Product Card Details
                    TC_PRODUCTS_003 - View Product Details Page
                    TC_PRODUCTS_004 - Add Product to Cart
                    TC_PRODUCTS_005 - Continue Shopping from Cart Modal
                    TC_PRODUCTS_006 - Filter Products by Category
                    TC_PRODUCTS_007 - Filter Products by Brand

    Given I am on the login page
    When I click "SignUp/Login" button
    And I enter valid "<email>" and "<password>"
    #TC_PRODUCTS_001
    When I go to Products page
    #TC_PRODUCTS_002
    And I check Product card details
    #TC_PRODUCTS_003
    And I view a product detail
    #TC_PRODUCTS_004
    And I add the product to cart


    @TC_PRODUCTS_001 @TC_PRODUCTS_002 @#TC_PRODUCTS_003
    Examples:
      | email           | password        |
      | buck@wild.com   | callofthewild   |

  @ProductsPage
  Scenario Outline: Validate that user can search an item in Products page
    Given I am on the login page
    When I click "SignUp/Login" button
    And I enter valid "<email>" and "<password>"
    #PPS-1
    When I go to Products page
    And Enter product name in search input and click search button
    Then I should see searched products

    @PPS-1
    Examples:
      | email           | password        |
      | buck@wild.com   | callofthewild   |
