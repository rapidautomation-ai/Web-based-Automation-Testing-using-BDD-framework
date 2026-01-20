package automation.test.stepsdefs;

import io.cucumber.java.en.When;
import automation.test.pages.LogInSignUpPage;
import automation.test.utils.BrowserUtils;
import automation.test.driverFactory.Driver;
import automation.test.hooks.Hooks;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;


public class LoginPageSteps {

    private final LogInSignUpPage loginPage = new LogInSignUpPage();

    @When("I enter valid {string} and {string}")
    public void iEnterValidCredentials(String email, String password) {
        try {
            // centralized init
            Hooks.initPage(loginPage);

            WebDriver driver = Driver.get();
            if (driver == null) {
                throw new RuntimeException("WebDriver is null. Ensure Driver is initialized before running steps.");
            }

            BrowserUtils.waitForVisibility(loginPage.loginEmailBox, 10);

            loginPage.loginEmailBox.clear();
            loginPage.loginEmailBox.sendKeys(email);

            loginPage.loginPasswordBox.clear();
            loginPage.loginPasswordBox.sendKeys(password);

            loginPage.loginButton.click();

//            try {
//                boolean popupHandled = BrowserUtils.handleAlertOrDomPopup(6); // waits up to 5s for native alert, then DOM popups
//                if (popupHandled) {
//                    System.out.println("[Popup Handler] popup detected and handled after login.");
//                } else {
//                    System.out.println("[Popup Handler] no popup detected after login; continuing.");
//                }
//            } catch (Exception ex) {
//                System.err.println("Error while handling popup after login: " + ex.getMessage());
//                throw ex;
//            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to enter valid credentials: " + e.getMessage(), e);
        }
    }

}
