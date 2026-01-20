package automation.test.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationReader {

    private static final Properties properties = new Properties(); // never null
    private static final Map<String, String> propsNormalized = new HashMap<>();
    private static final Map<String, String> FALLBACKS = new HashMap<>();

    static {
        // sensible fallback defaults (lowercase keys)
        FALLBACKS.put("browser", "chrome");
        FALLBACKS.put("baseurl", "https://www.automationexercise.com/");
        FALLBACKS.put("headless", "false");
        FALLBACKS.put("autonavigate", "true");

        // load configuration.properties from common locations (classpath -> src/test/resources -> project root)
        InputStream input = null;
        try {
            input = ConfigurationReader.class.getClassLoader().getResourceAsStream("configuration.properties");
            if (input == null) {
                try {
                    input = new FileInputStream("src/test/resources/configuration.properties");
                } catch (Exception ignored) {
                }
            }
            if (input == null) {
                try {
                    input = new FileInputStream("configuration.properties");
                } catch (Exception ignored) {
                }
            }

            if (input != null) {
                try (InputStream is = input) {
                    properties.load(is);
                }
                // normalize keys (lowercase) for fast case-insensitive lookup
                for (String key : properties.stringPropertyNames()) {
                    String val = properties.getProperty(key);
                    if (val != null) {
                        propsNormalized.put(key.toLowerCase(), val.trim());
                    }
                }
            } else {
                System.err.println("configuration.properties not found; using system properties and built-in defaults.");
            }
        } catch (Exception e) {
            System.err.println("Failed to load configuration.properties: " + e.getMessage());
        }
    }

    // Return a non-null, trimmed value; prefer system property, then file value, then fallback, then provided default.
    public static String get(String keyName) {
        return get(keyName, "");
    }

    public static String get(String keyName, String defaultValue) {
        if (keyName == null) {
            return safe(defaultValue);
        }
        String keyLower = keyName.trim().toLowerCase();

        // 1) system property override (exact key)
        String sys = System.getProperty(keyName);
        if (sys != null) {
            return safe(sys);
        }
        // 2) normalized properties map (case-insensitive)
        String prop = propsNormalized.get(keyLower);
        if (prop != null) {
            return safe(prop);
        }
        // 3) built-in fallbacks
        String fb = FALLBACKS.get(keyLower);
        if (fb != null) {
            return safe(fb);
        }
        // 4) provided default
        return safe(defaultValue);
    }

    public static boolean getBoolean(String keyName, boolean defaultValue) {
        String val = get(keyName, Boolean.toString(defaultValue));
        try {
            return Boolean.parseBoolean(val);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInt(String keyName, int defaultValue) {
        String val = get(keyName, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
