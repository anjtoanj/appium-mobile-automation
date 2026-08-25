package com.apidemos.e2e.core;

import com.apidemos.e2e.config.Config;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/** Builds a UiAutomator2 session from config.properties. */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static AndroidDriver create() {
        return new AndroidDriver(serverUrl(), options());
    }

    private static UiAutomator2Options options() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(Config.require("platform.name"))
                .setAutomationName(Config.require("automation.name"))
                .setDeviceName(Config.require("device.name"))
                .setAppPackage(Config.require("app.package"))
                .setAppActivity(Config.require("app.activity"))
                .setNewCommandTimeout(Config.getSeconds("new.command.timeout"));

        // The driver's default XPath 2.0 engine throws on several perfectly
        // legal XPath 1.0 axes. Appium's own error message recommends this
        // setting as the fix, and XPath 1.0 covers everything this suite needs.
        options.setCapability("appium:settings[enforceXPath1]", true);

        // Accept whatever activity the app lands on. Without these, a mismatch
        // between app.activity and the real launcher activity makes session
        // creation hang or quietly leave the device on the launcher.
        options.setCapability("appium:appWaitPackage", Config.require("app.package"));
        options.setCapability("appium:appWaitActivity", "*");

        options.setNoReset(Config.getBoolean("no.reset"));
        options.setFullReset(Config.getBoolean("full.reset"));

        String udid = Config.get("device.udid");
        if (!udid.isBlank()) {
            options.setUdid(udid);
        }

        // When app.path is set the APK is (re)installed; otherwise the copy
        // already on the device is launched via appPackage/appActivity.
        String appPath = Config.get("app.path");
        if (!appPath.isBlank()) {
            File apk = new File(appPath);
            if (!apk.isFile()) {
                throw new IllegalStateException("app.path does not point at a file: " + apk.getAbsolutePath());
            }
            options.setApp(apk.getAbsolutePath());
        }

        return options;
    }

    private static URL serverUrl() {
        String raw = Config.require("appium.server.url");
        try {
            return URI.create(raw).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalStateException("Invalid appium.server.url: " + raw, e);
        }
    }
}
