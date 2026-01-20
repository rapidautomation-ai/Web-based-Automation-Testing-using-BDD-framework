package automation.test.stepsdefs;

import automation.test.GenericFunctions.GenericFunctions;
import automation.test.driverFactory.Driver;
import automation.test.hooks.Hooks;
import automation.test.pages.CartPage;
import automation.test.utils.BrowserUtils;
import automation.test.utils.IframeHandler;
import automation.test.utils.Logs;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.util.Objects;

public class CartPageSteps {
    private final CartPage cartPage = new CartPage();
    public IframeHandler iframeHandler = new IframeHandler();

    @When("I go to cart page")
    public void iGoToCartPage() throws InterruptedException {
//        Hooks.initPage(iframeHandler);
//        BrowserUtils.waitForPageToLoad(10);
//        iframeHandler.adsIframe();
        Hooks.initPage(cartPage);
        Logs.test(Objects.equals(Driver.get().getTitle(), "Automation Exercise - Checkout"), "I am in Cart page");

        String productCartName = (String) GenericFunctions.Storage.get("productCartName");
        Logs.test(cartPage.cartProductName().getText().equals(productCartName), "Validate that product "+productCartName+" is displayed in cart");
    }

}
