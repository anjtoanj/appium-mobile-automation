package com.apidemos.e2e.core;

import io.appium.java_client.android.AndroidDriver;

import java.util.Collections;
import java.util.Map;

/**
 * Brings the app under test to the foreground, from a cold start.
 *
 * The driver's own implicit launch (appPackage + appActivity) cannot be relied
 * on: if the activity name does not resolve, or the app is already in an odd
 * state, the session still starts happily and simply leaves the device on the
 * launcher - which then fails every scenario on its first step.
 *
 * "mobile: activateApp" uses the package's own launcher intent, exactly as
 * tapping its icon would, so it does not care what the activity is called.
 *
 * Terminating and clearing app data first means each scenario starts from the
 * app's root screen AND from empty SharedPreferences. Without the clear, a
 * scenario that toggles a preference leaves it toggled for every scenario that
 * follows and for the next run of the suite - which makes results depend on
 * execution order.
 */
public final class AppLauncher {

    private AppLauncher() {
    }

    /**
     * Stops the app. Safe to call when it is not running.
     *
     * Needed explicitly at the end of a scenario: with noReset = true the
     * driver leaves the app running when the session ends, so without this the
     * app stays in the foreground between scenarios and after the suite.
     */
    public static void close(AndroidDriver driver, String appId) {
        try {
            driver.executeScript("mobile: terminateApp", Collections.singletonMap("appId", appId));
        } catch (RuntimeException wasNotRunning) {
            // Terminating an app that is not running is not a problem.
        }
    }

    public static void restart(AndroidDriver driver, String appId) {
        Map<String, Object> arg = Collections.singletonMap("appId", appId);

        close(driver, appId);

        try {
            driver.executeScript("mobile: clearApp", arg);
        } catch (RuntimeException unavailable) {
            // Older uiautomator2 drivers lack this. Say so loudly rather than
            // letting scenarios silently share state.
            System.err.println("AppLauncher: could not clear app data for " + appId
                    + " (" + unavailable.getMessage() + "). Scenarios will share "
                    + "preferences - steps must not assume a clean slate.");
        }

        driver.executeScript("mobile: activateApp", arg);
    }

}
