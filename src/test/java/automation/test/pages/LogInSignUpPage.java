package automation.test.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LogInSignUpPage extends BasePage {
    @FindBy(css = "input[data-qa='login-email']")
    public WebElement loginEmailBox;
    @FindBy(css = "input[data-qa='login-password']")
    public WebElement loginPasswordBox;
    @FindBy(css = "button[data-qa='login-button']")
    public WebElement loginButton;
}
