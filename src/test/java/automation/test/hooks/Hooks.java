package automation.test.hooks;

import automation.test.driverFactory.Driver;
import automation.test.utils.ConfigurationReader;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.Set;

public class Hooks {

    // keep a reference to the WebDriver instance created for this scenario
    private WebDriver thisDriver;

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        // determine per-scenario browser:
        // - look for tags like @browser=firefox or @browser_firefox
        // - fall back to system property -Dbrowser=...
        // - fall back to configuration.properties
        String browser = null;

        try {
            Set<String> tags = (Set<String>) scenario.getSourceTagNames();
            for (String t : tags) {
                if (t == null) continue;
                if (t.startsWith("@browser=")) {
                    browser = t.substring("@browser=".length());
                    break;
                } else if (t.startsWith("@browser_")) {
                    browser = t.substring("@browser_".length());
                    break;
                }
            }
        } catch (Exception ignored) {}

        if (browser == null || browser.isEmpty()) {
            browser = System.getProperty("browser", "");
            if (browser == null || browser.isEmpty()) {
                browser = ConfigurationReader.get("browser", "chrome");
            }
        }

        // set per-thread browser override (call before Driver.get())
        Driver.setBrowserForCurrentThread(browser);

        // create driver for this scenario/thread
        try {
            thisDriver = Driver.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create WebDriver in @Before hook: " + e.getMessage(), e);
        }

        try {
            thisDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(6));
            thisDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
            try { thisDriver.manage().window().maximize(); } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        // attach screenshot on failure if possible, using the same driver instance created in @Before
        if (scenario.isFailed() && thisDriver != null) {
            try {
                final byte[] screenshot = ((TakesScreenshot) thisDriver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "screenshot");
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }

        // always attempt to close the scenario's driver
        try {
            Driver.closeDriver();
        } catch (Exception e) {
            System.err.println("Failed to close WebDriver in @After: " + e.getMessage());
        } finally {
            thisDriver = null;
        }
    }

    @AfterAll
    public static void afterAll() {
        try {
            Driver.closeAll();
        } catch (Exception e) {
            System.err.println("Failed to close WebDriver in @AfterAll: " + e.getMessage());
        }
    }

    /**
     * Centralized helper to initialize PageFactory for any page object.
     * Call Hooks.initPage(myPage) from step methods instead of having per-step ensurePageInitialized().
     */
    public static void initPage(Object page) {
        WebDriver driver = Driver.get();
        if (driver == null) {
            throw new RuntimeException("WebDriver instance is null. Ensure Driver.get() initializes the browser in @Before hook.");
        }
        PageFactory.initElements(driver, page);
    }

}
