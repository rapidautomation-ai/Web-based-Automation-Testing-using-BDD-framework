package automation.test.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LogInSignUpPage extends BasePage {
    @FindBy(xpath = "//input[@placeholder=\"Username\"]")
    public WebElement loginUserNameBox;
    @FindBy(xpath = "//input[@placeholder=\"Password\"]")
    public WebElement loginPasswordBox;
    @FindBy(xpath = "//input[@id=\"login-button\"]")
    public WebElement loginButton;
}
