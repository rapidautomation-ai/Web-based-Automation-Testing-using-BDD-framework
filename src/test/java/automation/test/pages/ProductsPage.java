package automation.test.pages;

import automation.test.driverFactory.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProductsPage {
    @FindBy(xpath = "(//iframe)[9]")
    public WebElement adIframe1;
    @FindBy(xpath = "(//iframe)[8]")
    public WebElement adIframe2;
    @FindBy(xpath = "//div[@id=\"dismiss-button\"]")
    public WebElement adCloseBtn;
    @FindBy(css="a[href='/products']")
    public WebElement productsLink;

    // locator for product containers (useable for counting)
    public By allProductsPage = By.xpath("//div[@class=\"productinfo text-center\"]");

    // return the product name element (h2) at 1-based index
    public WebElement selectedProductName(int index){
        return Driver.get().findElement(By.xpath("(//div[@class=\"productinfo text-center\"]//p)[" + index + "]"));
    }
    public WebElement selectedProductImage(int index){
        return Driver.get().findElement(By.xpath("(//div[@class=\"productinfo text-center\"]//img)[" + index + "]"));
    }
    public WebElement selectedProductPrice(int index){
        return Driver.get().findElement(By.xpath("(//div[@class=\"productinfo text-center\"]//h2)[" + index + "]"));
    }
    public WebElement selectedProductAddToCart(int index){
        return Driver.get().findElement(By.xpath("(//div[@class=\"productinfo text-center\"]//a)[" + index + "]"));
    }
    // return the 'View Product' button element at 1-based index
    public WebElement viewProductButton(int index){
        return Driver.get().findElement(By.xpath("(//a[.='View Product'])[" + index + "]"));
    }

    // return the product name element matching the provided text
    public WebElement selectedProductName(String detail){
        return Driver.get().findElement(By.xpath("//div[@class='col-sm-4']//h2[normalize-space()='" + detail + "']"));
    }

    @FindBy(xpath = "//div[@class=\"product-information\"]//h2")
    public WebElement productDetailName;
    @FindBy(xpath = "(//div[@class=\"product-information\"]//p)[1]")
    public WebElement productDetailCategory;
    @FindBy(xpath = "//div[@class=\"product-information\"]//span[contains(text(),'Rs')]")
    public WebElement productDetailPrice;
    @FindBy(xpath = "//div[@class=\"product-information\"]//span//input[@type='number']")
    public WebElement productDetailQuantity;
    @FindBy(xpath = "//div[@class=\"product-information\"]/p[contains(text(),'In Stock')]")
    public WebElement productDetailAvailability;
    @FindBy(xpath = "//div[@class=\"product-information\"]/p[contains(text(),'New')]")
    public WebElement productDetailCondition;
    @FindBy(xpath = "(//div[@class=\"product-information\"]/p)[4]")
    public WebElement productDetailBrand;
    @FindBy(xpath = "//div[@class=\"product-information\"]//span//button")
    public WebElement productDetailAddToCartButton;

    @FindBy(id = "cartModal")
    public WebElement addToCartModal;
    @FindBy(xpath = "//div[@id=\"cartModal\"]//a")
    public WebElement viewCartButton;
    @FindBy(xpath = "//button[.='Continue Shopping']")
    public WebElement continueShoppingButton;

    @FindBy(xpath = "//input[@id=\"search_product\"]")
    public WebElement searchProductBox;

    @FindBy(id = "submit_search")
    public WebElement searchButton;


}
