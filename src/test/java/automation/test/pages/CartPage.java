package automation.test.pages;

import automation.test.GenericFunctions.GenericFunctions;
import automation.test.driverFactory.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CartPage {
    public WebElement cartProductName() {
        String productCartName = (String) GenericFunctions.Storage.get("productCartName");
        return Driver.get().findElement(By.xpath("//td[@class='cart_description']//h4//a[text()='" + productCartName + "']"));
    }
}
