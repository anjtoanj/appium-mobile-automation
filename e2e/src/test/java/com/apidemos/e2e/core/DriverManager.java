package com.apidemos.e2e.core;

import io.appium.java_client.android.AndroidDriver;

/**
 * Holds the driver for the current thread so page objects and step definitions
 * can reach it without passing it around, and so the suite stays safe to run
 * with parallel Cucumber scenarios later on.
 */
public final class DriverManager {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void set(AndroidDriver driver) {
        DRIVER.set(driver);
    }

    public static AndroidDriver get() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "No driver for this thread. Is the Cucumber @Before hook (Hooks) registered in the glue path?");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
