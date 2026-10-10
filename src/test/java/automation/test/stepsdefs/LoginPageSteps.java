package automation.test.stepsdefs;

import io.cucumber.java.en.When;
import automation.test.pages.LogInSignUpPage;
import automation.test.utils.BrowserUtils;
import automation.test.driverFactory.Driver;
import automation.test.hooks.Hooks;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import automation.test.utils.Logs;


public class LoginPageSteps {

    private final LogInSignUpPage loginPage = new LogInSignUpPage();
    private final WebDriver driver = Driver.get();

    @When("I enter valid {string} and {string}")
    public void iEnterValidCredentials(String username, String password) {
        try {

            BrowserUtils.waitForVisibility(loginPage.loginUserNameBox, 10);

            loginPage.loginUserNameBox.clear();
            loginPage.loginUserNameBox.sendKeys(username);

            loginPage.loginPasswordBox.clear();
            loginPage.loginPasswordBox.sendKeys(password);

            loginPage.loginButton.click();

            Logs.test(true, "Entered valid credentials and clicked login button");

        } catch (Exception e) {
            throw new RuntimeException("Failed to enter valid credentials: " + e.getMessage(), e);
        }
    }

}
