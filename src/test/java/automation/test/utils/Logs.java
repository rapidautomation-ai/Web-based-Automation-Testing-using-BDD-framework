package automation.test.utils;

/**
 * Global logging + validation helper.
 * Usage examples:
 *   Logs.verifyTrue(condition, "I am in home page");
 *   Logs.verifyEquals(expected, actual, "Home page title");
 *   Logs.pass("Reached checkout");
 *   Logs.fail("Could not login"); // will throw AssertionError by default
 */
public final class Logs {
    // If true, failed validations will throw AssertionError (stop test). If false, they only log.
    private static volatile boolean throwOnFailure = true;

    private Logs() { /* utility */ }

    public static void setThrowOnFailure(boolean shouldThrow) {
        throwOnFailure = shouldThrow;
    }

    public static boolean isThrowOnFailure() {
        return throwOnFailure;
    }

    public static void info(String message) {
        System.out.println("[Info] " + message);
    }

    public static void warn(String message) {
        System.out.println("[Warning] " + message);
    }

    public static void test(Boolean condition, String message) {
        int retries = 0;
        final int maxRetries = 3;
        while (retries < maxRetries) {
            try {
                if (Boolean.TRUE.equals(condition)) {
                    System.out.println("[Passed] " + message);
                    return;
                } else {
                    retries++;
                    warn("Validation error: " + message + " | attempt " + retries + " of " + maxRetries);
                    // short pause to allow transient issues to resolve
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        // break and treat as failure
                        break;
                    }
                    // Note: condition is evaluated once by the caller; if callers want re-evaluation across retries
                    // they should refactor to pass a Supplier<Boolean> or re-call this method with updated condition.
                }
            } catch (Exception e) {
                // unexpected exception during validation attempt
                retries++;
                warn("Exception during validation attempt " + retries + ": " + e.getMessage());
            }
        }

        // retries exhausted -> fail the test run
        fail(message + " (failed after " + retries + " attempts)");
    }

    public static void fail(String message) {
        System.err.println("[Failed] " + message);
        if (throwOnFailure) {
            throw new AssertionError(message);
        }

    }
}
