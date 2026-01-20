package automation.test.driverFactory;

import automation.test.utils.ConfigurationReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Driver {

    private Driver(){}

    // per-thread WebDriver
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // optional per-thread browser override (set from Hooks)
    private static final ThreadLocal<String> browserThreadLocal = new ThreadLocal<>();

    // track created drivers for best-effort global cleanup
    private static final Set<WebDriver> allDrivers = ConcurrentHashMap.newKeySet();

    // ensure JVM shutdown attempts cleanup
//    static {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                closeAll();
//            } catch (Exception ignored) {}
//        }));
//    }

    // allow Hooks to set desired browser for the current thread (call before Driver.get())
    public static void setBrowserForCurrentThread(String browser) {
        if (browser == null) {
            browserThreadLocal.remove();
            return;
        }
        browserThreadLocal.set(browser.trim().toLowerCase());
    }

    public static synchronized WebDriver get(){
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            return driver;
        }

        // determine browser: per-thread override -> system property -> config -> default
        String browser = browserThreadLocal.get();
        if (browser == null || browser.isEmpty()) {
            browser = System.getProperty("browser", "");
        }
        if (browser == null || browser.isEmpty()) {
            browser = ConfigurationReader.get("browser", "chrome");
        }
        if (browser == null) browser = "chrome";
        browser = browser.trim().toLowerCase();

        String baseUrl = ConfigurationReader.get("baseUrl", "https://www.automationexercise.com/");
        boolean autoNavigate = "true".equalsIgnoreCase(ConfigurationReader.get("autoNavigate", "true"));

        try {
            switch (browser){
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();

                    options.addArguments("--remote-allow-origins=*");
                    options.addArguments("--start-maximized");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--disable-notifications");
                    options.addArguments("--disable-infobars");
                    options.addArguments("--disable-extensions");
                    options.addArguments("--disable-popup-blocking");
                    driver = new ChromeDriver(options);
                    break;
                case "chrome-headless":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions headlessOptions = new ChromeOptions();
                    headlessOptions.addArguments("--remote-allow-origins=*");
                    headlessOptions.addArguments("--headless=new");
                    headlessOptions.addArguments("--window-size=1920,1080");
                    headlessOptions.addArguments("--no-sandbox");
                    headlessOptions.addArguments("--disable-dev-shm-usage");
                    headlessOptions.addArguments("--disable-notifications");
                    headlessOptions.addArguments("--disable-infobars");
                    headlessOptions.addArguments("--disable-extensions");
                    headlessOptions.addArguments("--disable-popup-blocking");
                    driver = new ChromeDriver(headlessOptions);
                    break;
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    break;
                case "firefox-headless":
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver(); // add options if needed
                    break;
                case "ie":
                    if (!System.getProperty("os.name").toLowerCase().contains("windows"))
                        throw new WebDriverException("Your OS doesn't support Internet Explorer");
                    WebDriverManager.iedriver().setup();
                    driver = new InternetExplorerDriver();
                    break;
                case "edge":
                    // Try WebDriverManager first; if it fails and no local path provided, fail with a clear error (no fallback to Chrome)
                    try {
                        WebDriverManager.edgedriver().setup();
                        driver = new EdgeDriver();
                    } catch (Exception wdmEx) {
                        String edgeDriverPath = ConfigurationReader.get("edgeDriverPath", "").trim();
                        if (!edgeDriverPath.isEmpty()) {
                            System.err.println("WebDriverManager failed for Edge: " + wdmEx.getMessage());
                            System.err.println("Attempting to use local EdgeDriver binary from configuration: " + edgeDriverPath);
                            System.setProperty("webdriver.edge.driver", edgeDriverPath);
                            driver = new EdgeDriver();
                        } else {
                            String msg = "Failed to initialize WebDriver for browser='edge': WebDriverManager failed to download Edge driver (network/DNS).\n"
                                    + "Provide a local Edge driver via configuration property 'edgeDriverPath' or run tests with a different browser.";
                            throw new RuntimeException(msg, wdmEx);
                        }
                    }
                    break;
                case "safari":
                    if (!System.getProperty("os.name").toLowerCase().contains("mac"))
                        throw new WebDriverException("Your OS doesn't support Safari");
                    WebDriverManager.getInstance(SafariDriver.class).setup();
                    driver = new SafariDriver();
                    break;
                default:
                    // Do not silently fallback to Chrome — surface a clear error for unsupported/unknown browser values.
                    throw new RuntimeException("Unknown or unsupported browser '" + browser +
                            "'. Supported values: chrome, chrome-headless, firefox, firefox-headless, edge, safari, ie");
            }

            // basic timeouts & maximize where applicable
            try {
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
                String headlessFlag = ConfigurationReader.get("headless", "false");
                if (!("true".equalsIgnoreCase(headlessFlag) || browser.contains("headless"))) {
                    try { driver.manage().window().maximize(); } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}

            // store in threadlocal and tracked set
            driverThreadLocal.set(driver);
            allDrivers.add(driver);

            // navigate automatically if requested
            if (autoNavigate && baseUrl != null && !baseUrl.isEmpty()) {
                try {
                    driver.get(baseUrl);
                } catch (Exception navEx) {
                    try { driver.quit(); } catch (Exception ignored) {}
                    driverThreadLocal.remove();
                    allDrivers.remove(driver);
                    throw new RuntimeException("Failed to navigate to baseUrl='" + baseUrl + "'", navEx);
                }
            }

            return driver;

        } catch (Exception e) {
            // ensure thread local cleanup on failure
            driverThreadLocal.remove();
            throw new RuntimeException("Failed to initialize WebDriver for browser='" + browser + "': " + e.getMessage(), e);
        }
    }

    // close and clear the thread-local driver
    public static synchronized void closeDriver(){
        WebDriver driver = driverThreadLocal.get();
        if(driver != null){
            try {
                driver.quit();
            } catch (Exception ignored) {}
            driverThreadLocal.remove();
            allDrivers.remove(driver);
        }
        // also clear browser override for this thread
        browserThreadLocal.remove();
    }

    // best-effort: close any remaining drivers across threads
    public static synchronized void closeAll() {
        for (WebDriver d : allDrivers) {
            if (d == null) continue;
            try {
                d.quit();
            } catch (Exception ignored) {}
        }
        allDrivers.clear();
        driverThreadLocal.remove();
        browserThreadLocal.remove();
    }
}
