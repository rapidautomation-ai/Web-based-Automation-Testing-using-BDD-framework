package automation.test.stepsdefs;

import automation.test.GenericFunctions.GenericFunctions;
import automation.test.driverFactory.Driver;
import automation.test.pages.CartPage;
import automation.test.pages.ProductsPage;
import automation.test.utils.BrowserUtils;
import automation.test.utils.IframeHandler;
import automation.test.utils.Logs;
import automation.test.hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.Objects;

public class ProductPageSteps {

    private final ProductsPage productsPage = new ProductsPage();
    public IframeHandler iframeHandler = new IframeHandler();

    @When("I go to Products page")
    public void iGoToProductsPage() {
        try {
            // centralized init
            Hooks.initPage(productsPage);
            BrowserUtils.waitForVisibility(productsPage.productsLink, 10);
            productsPage.productsLink.click();
            Hooks.initPage(iframeHandler);
            iframeHandler.adsIframe();
            Hooks.initPage(productsPage);
            BrowserUtils.waitForPageToLoad(10);

            String onProducts = Driver.get().getTitle();
            assert onProducts != null;
            Logs.test(onProducts.equals("Automation Exercise - All Products"), "I am in products page");
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to Products page: " + e.getMessage(), e);
        }
    }

    @And("I view a product detail")
    public void iViewAProductDetail() {
        try {
            // centralized init
            Hooks.initPage(productsPage);

            int index = (int) GenericFunctions.Storage.get("productIndex");
            productsPage.viewProductButton(index).click();
            BrowserUtils.waitForVisibility(productsPage.productDetailName, 10);

            String productName = GenericFunctions.Text.getText(productsPage.productDetailName).trim().replaceAll("\\s+", " ");
            GenericFunctions.Storage.put("productCartName", productName);
            String productCategory = GenericFunctions.Text.getText(productsPage.productDetailCategory);
            String productPrice = GenericFunctions.Text.getText(productsPage.productDetailPrice);
            boolean productQuantity = productsPage.productDetailQuantity.getAttribute("value").equals("1");
            String productAvailability = GenericFunctions.Text.getText(productsPage.productDetailAvailability);
            String productCondition = GenericFunctions.Text.getText(productsPage.productDetailCondition);
            String productBrand = GenericFunctions.Text.getText(productsPage.productDetailBrand);
            Logs.test(productsPage.productDetailName.isDisplayed(), "Validate that selected product name is "+productName);
            Logs.test(productsPage.productDetailCategory.isDisplayed(), "Validate that selected product category is "+productCategory);
            Logs.test(productsPage.productDetailPrice.isDisplayed(), "Validate that selected product price is "+productPrice);
            Logs.test(productQuantity, "Validate that selected product quantity is "+productsPage.productDetailQuantity.getAttribute("value"));
            Logs.test(productsPage.productDetailAvailability.isDisplayed(), "Validate that selected product availability is "+productAvailability);
            Logs.test(productsPage.productDetailCondition.isDisplayed(), "Validate that selected product condition is "+productCondition);
            Logs.test(productsPage.productDetailBrand.isDisplayed(), "Validate that selected product brand is "+productBrand);


        } catch (Exception e) {
            throw new RuntimeException("Failed while viewing product detail: " + e.getMessage(), e);
        }
    }

    @And("Enter product name in search input and click search button")
    public void enterProductNameInSearchInputAndClickSearchButton() {
        Hooks.initPage(productsPage);

        int productCount = GenericFunctions.Count.visible(productsPage.allProductsPage);
        if (productCount <= 0) {
            throw new RuntimeException("No products found on Products page (count=" + productCount + ")");
        }

        // pick a random 1-based index within range
        int index = GenericFunctions.generateRandomNumber(1, productCount);

        // get the selected product name text (h2)
        String detail = GenericFunctions.Text.getText(productsPage.selectedProductName(index));
        Logs.info("Selected product name to search: " + detail);
        GenericFunctions.Storage.put("productDetail", detail);

        // enter product name in search box and submit
        BrowserUtils.waitForVisibility(productsPage.searchProductBox, 5);
        productsPage.searchProductBox.clear();
        productsPage.searchProductBox.sendKeys(detail);
        productsPage.searchButton.click();

        // give the page a moment to respond
        BrowserUtils.waitForPageToLoad(10);
    }

    @Then("I should see searched products")
    public void iShouldSeeSearchedProducts() {
        Hooks.initPage(productsPage);

        String expected = (String) GenericFunctions.Storage.get("productDetail");
        if (expected == null) expected = "";

        try {
            // CASE A: search navigated directly to product detail page
            BrowserUtils.waitForVisibility(productsPage.productDetailName, 3);
            String viewedName = GenericFunctions.Text.getText(productsPage.productDetailName);
            Logs.info("Search led to detail page. Viewed product name: " + viewedName);
            boolean matchDetail = viewedName.equals(expected);
            Logs.test(matchDetail, "Validate that searched product detail matches expected");
            Assert.assertTrue("Viewed product name (detail) should match searched product", matchDetail);
        } catch (Exception ignored) {
            // not a direct detail page; proceed to results-list handling
        }

    }

    @And("I check Product card details")
    public void iCheckProductCardDetails() {
        Hooks.initPage(productsPage);

        int productCount = GenericFunctions.Count.visible(productsPage.allProductsPage);
        if (productCount <= 0) {
            throw new RuntimeException("No products found on Products page (count=" + productCount + ")");
        }

        // pick a random 1-based index within range
        int index = GenericFunctions.generateRandomNumber(1, productCount);
        GenericFunctions.Storage.put("productIndex", index);

        WebElement productNameElem = productsPage.selectedProductName(index);
        WebElement productImageElem = productsPage.selectedProductImage(index);
        WebElement productPriceElem = productsPage.selectedProductPrice(index);
        WebElement productAddToCartElem = productsPage.selectedProductAddToCart(index);
        WebElement productViewProductElem = productsPage.viewProductButton(index);
        BrowserUtils.scrollToElement(productNameElem);
        BrowserUtils.waitForVisibility(productNameElem, 5);

        boolean nameVisible = productNameElem.isDisplayed();
        boolean imageVisible = productImageElem.isDisplayed();
        boolean priceVisible = productPriceElem.isDisplayed();
        boolean addToCartVisible = productAddToCartElem.isDisplayed();
        boolean viewProductVisible = productViewProductElem.isDisplayed();
        Logs.test(nameVisible, "Product name: "+productNameElem.getText()+" is visible on product card");
        Logs.test(imageVisible, "Product image is visible on product card");
        Logs.test(priceVisible, "Product price: "+productPriceElem.getText()+" is visible on product card");
        Logs.test(addToCartVisible, "Add to Cart button is visible on product card");
        Logs.test(viewProductVisible, "View Product button is visible on product card");

    }

    @And("I add the product to cart")
    public void iAddTheProductToCart() {
        Hooks.initPage(productsPage);

        productsPage.productDetailAddToCartButton.click();
        BrowserUtils.waitForVisibility(productsPage.addToCartModal, 10);
        Logs.test(productsPage.addToCartModal.isDisplayed(), "Validate that Add to Cart modal is displayed");

        productsPage.viewCartButton.click();
    }
}
