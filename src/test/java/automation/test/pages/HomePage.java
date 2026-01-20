package automation.test.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage {
    @FindBy(css = "a[href='/login']")
    public WebElement loginSignupLink;
}
