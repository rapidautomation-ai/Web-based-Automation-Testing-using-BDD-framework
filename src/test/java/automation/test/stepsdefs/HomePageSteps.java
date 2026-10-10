package automation.test.stepsdefs;

import automation.test.driverFactory.Driver;
import automation.test.pages.HomePage;
import automation.test.utils.Logs;
import automation.test.hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.Assert;
import automation.test.utils.BrowserUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class HomePageSteps {
    private final HomePage homePage = new HomePage();
    private final WebDriver driver = Driver.get();

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        Logs.info("Initializing browser and environment");

        BrowserUtils.waitForPageToLoad(10);

        String expectedUrl = "https://www.saucedemo.com/";
        // navigate explicitly if not already on the site
        try {
            if (driver.getCurrentUrl() == null || !driver.getCurrentUrl().equalsIgnoreCase(expectedUrl)) {
                driver.get(expectedUrl);
            }
            BrowserUtils.waitForPageToLoad(10);
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to base URL: " + e.getMessage(), e);
        }

        Assert.assertTrue("Expected URL to be correct", driver.getCurrentUrl().equalsIgnoreCase(expectedUrl));
        Logs.test("Swag Labs".equals(driver.getTitle()),"Landed on Home page");
    }

    @When("I click {string} button")
    public void iClickButton(String option) {
        // centralized init

        WebDriver driver = Driver.get();
        if (driver == null) {
            throw new RuntimeException("WebDriver is null. Ensure Driver is initialized before running steps.");
        }

        // wait for the link element to be visible, then click it (avoid operating on null element)
        BrowserUtils.waitForVisibility(homePage.loginSignupLink, 10);
        homePage.loginSignupLink.click();

        BrowserUtils.waitForPageToLoad(10);

        // verify title contains expected text after clicking signup/login
        String title = driver.getTitle() == null ? "" : driver.getTitle();
        boolean landed = title.contains("Signup") || title.contains("Login") || title.contains("Signup / Login");
        Logs.test(landed, "Landed on Signup/Login page");
    }
}