package automation.test.utils;

import automation.test.driverFactory.Driver;
import automation.test.hooks.Hooks;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class IframeHandler {

    @FindBy(xpath = "(//iframe)[8]")
    public WebElement iframeElement1;

    @FindBy(xpath = "(//iframe)[9]")
    public WebElement iframeElement2;

    @FindBy(xpath = "(//iframe[@title=\"Advertisement\"])[4]")
    public WebElement iframeElement3;

    @FindBy(xpath = "//div[@id=\"dismiss-button\"]")
    public WebElement adCloseBtn;

    public void adsIframe() throws InterruptedException {
        WebDriver driver = Driver.get();

        Thread.sleep(5000);
        if (iframeElement1.isDisplayed()) {
            driver.switchTo().frame(iframeElement1);
            Logs.info("Switched to iframe 1");
            Thread.sleep(2000); // Wait for the ad to load
            adCloseBtn.click();
            Logs.info("Successfully closed the iframe ad");
            driver.switchTo().defaultContent();
        } else if (iframeElement2.isDisplayed()) {
            driver.switchTo().frame(iframeElement2);
            Logs.info("Switched to iframe 2");
            Thread.sleep(2000); // Wait for the ad to load
            adCloseBtn.click();
            Logs.info("Successfully closed the iframe ad");
            driver.switchTo().defaultContent();
        } else if (iframeElement3.isDisplayed()) {
            driver.switchTo().frame(iframeElement3);
            Logs.info("Switched to iframe 3");
            Thread.sleep(2000); // Wait for the ad to load
            adCloseBtn.click();
            Logs.info("Successfully closed the iframe ad");
            driver.switchTo().defaultContent();
        } else {
            Logs.info("No iframe displayed");
        }
    }
}
