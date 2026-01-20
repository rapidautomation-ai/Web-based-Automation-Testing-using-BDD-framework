package automation.test.GenericFunctions;

import automation.test.driverFactory.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Consolidated utility container:
 * - GenericFunctions.Count : element counting helpers
 * - GenericFunctions.Storage : per-thread and global storage helpers
 *
 * Existing Count and Storage classes are left as delegators to preserve compatibility.
 */
public final class GenericFunctions {

    private GenericFunctions() {
        // utility holder
    }

    /**
     * Generate a random integer between start and end (inclusive).
     * Throws IllegalArgumentException if start > end.
     *
     * Usage:
     *   int n = GenericFunctions.generateRandomNumber(1, 10);
     */
    public static int generateRandomNumber(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("generateRandomNumber: start must be <= end (start=" + start + ", end=" + end + ")");
        }
        return ThreadLocalRandom.current().nextInt(start, end + 1);
    }



    public static final class Count {

        private Count() {
            // utility
        }

        private static WebDriver driver() {
            WebDriver d = Driver.get();
            if (d == null) {
                throw new RuntimeException("WebDriver instance is null. Ensure the driver is initialized before calling Count methods.");
            }
            return d;
        }

        // Basic counts ---------------------------------------------------------------

        public static int by(By locator) {
            List<WebElement> elems = driver().findElements(locator);
            return elems == null ? 0 : elems.size();
        }

        public static int byCss(String cssSelector) {
            return by(By.cssSelector(cssSelector));
        }

        public static int byXpath(String xpath) {
            return by(By.xpath(xpath));
        }

        // Visible/displayed counts --------------------------------------------------

        public static int visible(By locator) {
            List<WebElement> elems = driver().findElements(locator);
            if (elems == null || elems.isEmpty()) return 0;
            int count = 0;
            for (WebElement e : elems) {
                try {
                    if (e != null && e.isDisplayed()) count++;
                } catch (Exception ignored) {
                    // element detached / stale -> treat as not visible
                }
            }
            return count;
        }


        public static int visibleCss(String cssSelector) {
            return visible(By.cssSelector(cssSelector));
        }

        public static int visibleXpath(String xpath) {
            return visible(By.xpath(xpath));
        }

        public static int visibleXpath(WebElement xpath) {
            return visible(By.xpath(String.valueOf(xpath)));
        }

        // Counts with explicit wait -------------------------------------------------

        public static int withWait(By locator, int timeoutSeconds) {
            WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(Math.max(0, timeoutSeconds)));
            List<WebElement> elems = wait.until(d -> d.findElements(locator));
            return elems == null ? 0 : elems.size();
        }

        public static int visibleWithWait(By locator, int timeoutSeconds) {
            WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(Math.max(0, timeoutSeconds)));
            List<WebElement> elems = wait.until(d -> d.findElements(locator));
            if (elems == null || elems.isEmpty()) return 0;
            int count = 0;
            for (WebElement e : elems) {
                try {
                    if (e != null && e.isDisplayed()) count++;
                } catch (Exception ignored) {
                }
            }
            return count;
        }
    }

    // Thread-local and global storage --------------------------------------------

    public static final class Storage {

        private static final ThreadLocal<Map<String, Object>> threadStore =
                ThreadLocal.withInitial(ConcurrentHashMap::new);

        private static final Map<String, Object> globalStore = new ConcurrentHashMap<>();

        private Storage() {
            // utility class
        }

        // Per-thread (scenario) helpers ------------------------------------------------

        public static void put(String key, int value) {
            if (key == null) return;
            threadStore.get().put(key, value);
        }

        public static void put(String key, String value) {
            if (key == null) return;
            threadStore.get().put(key, value);
        }

        public static Object get(String key) {
            if (key == null) return null;
            return threadStore.get().get(key);
        }

        @SuppressWarnings("unchecked")
        public static <T> T get(String key, Class<T> cls) {
            Object val = get(key);
            if (val == null) return null;
            return cls.cast(val);
        }

        @SuppressWarnings("unchecked")
        public static <T> T getOrDefault(String key, T defaultValue) {
            Object val = get(key);
            return val == null ? defaultValue : (T) val;
        }

        public static boolean contains(String key) {
            return key != null && threadStore.get().containsKey(key);
        }

        public static void remove(String key) {
            if (key == null) return;
            threadStore.get().remove(key);
        }

        public static void clear() {
            threadStore.get().clear();
            threadStore.remove();
        }

        // Global helpers --------------------------------------------------------------

        public static void putGlobal(String key, Object value) {
            if (key == null) return;
            globalStore.put(key, value);
        }

        public static Object getGlobal(String key) {
            if (key == null) return null;
            return globalStore.get(key);
        }

        @SuppressWarnings("unchecked")
        public static <T> T getGlobal(String key, Class<T> cls) {
            Object val = getGlobal(key);
            if (val == null) return null;
            return cls.cast(val);
        }

        public static boolean containsGlobal(String key) {
            return key != null && globalStore.containsKey(key);
        }

        public static void removeGlobal(String key) {
            if (key == null) return;
            globalStore.remove(key);
        }

        public static void clearGlobal() {
            globalStore.clear();
        }
    }

    public static final class Text {

        private Text() {
            // utility
        }

        private static WebDriver driver() {
            WebDriver d = Driver.get();
            if (d == null) {
                throw new RuntimeException("WebDriver instance is null. Ensure the driver is initialized before calling Text methods.");
            }
            return d;
        }

        /**
         * Return the visible text of the first element matching locator,
         * trimmed. Returns empty string if no element is found or any error occurs.
         */
        public static String getText(By locator) {
            try {
                List<WebElement> elems = driver().findElements(locator);
                if (elems == null || elems.isEmpty()) return "";
                WebElement e = elems.get(0);
                return e == null ? "" : safeText(e.getText());
            } catch (Exception ignored) {
                return "";
            }
        }

        /**
         * Wait up to timeoutSeconds for a visible element to appear, then return its trimmed text.
         * Returns empty string on timeout or error.
         */
        public static String getText(By locator, int timeoutSeconds) {
            try {
                WebDriverWait wait = new WebDriverWait(driver(), Duration.ofSeconds(Math.max(0, timeoutSeconds)));
                WebElement el = wait.until(d -> {
                    List<WebElement> list = d.findElements(locator);
                    if (list == null || list.isEmpty()) return null;
                    WebElement first = list.get(0);
                    try {
                        return (first != null && first.isDisplayed()) ? first : null;
                    } catch (Exception ex) {
                        return null;
                    }
                });
                return el == null ? "" : safeText(el.getText());
            } catch (Exception ignored) {
                return "";
            }
        }

        /**
         * Return trimmed text for a provided WebElement, safe for nulls/exceptions.
         */
        public static String getText(WebElement element) {
            if (element == null) return "";
            try {
                return safeText(element.getText());
            } catch (Exception ignored) {
                return "";
            }
        }

        private static String safeText(String s) {
            return s == null ? "" : s.trim();
        }
    }
}
