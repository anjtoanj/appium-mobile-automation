package com.apidemos.e2e.pages;

import com.apidemos.e2e.config.Config;
import com.apidemos.e2e.core.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared behaviour for every screen: the driver, an explicit wait, PageFactory
 * wiring for {@code @AndroidFindBy} fields, and the scroll/tap helpers that the
 * ApiDemos list screens need.
 */
public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;
    protected final Duration timeout;

    protected BasePage() {
        this.driver = DriverManager.get();
        this.timeout = Config.getSeconds("element.timeout");
        this.wait = new WebDriverWait(driver, timeout);
        PageFactory.initElements(new AppiumFieldDecorator(driver, timeout), this);
    }

    // ------------------------------------------------------------- locators --

    /** Exact-text UiSelector. Cheap, and the ApiDemos lists are all text. */
    protected static By byText(String text) {
        return AppiumBy.androidUIAutomator(
                String.format("new UiSelector().text(\"%s\")", escape(text)));
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // -------------------------------------------------------------- actions --

    /**
     * Finds an element by its visible text, scrolling the list if it is not
     * already on screen. Tries the direct selector first because UiScrollable
     * throws when the screen has nothing scrollable on it.
     */
    protected WebElement findByTextScrolling(String text) {
        List<WebElement> onScreen = driver.findElements(byText(text));
        if (!onScreen.isEmpty()) {
            return onScreen.get(0);
        }
        try {
            return driver.findElement(AppiumBy.androidUIAutomator(String.format(
                    "new UiScrollable(new UiSelector().scrollable(true).instance(0))"
                            + ".scrollIntoView(new UiSelector().text(\"%s\"))", escape(text))));
        } catch (Exception scrollFailed) {
            throw new NoSuchElementException(
                    "No element with text '" + text + "' after scrolling the screen", scrollFailed);
        }
    }

    protected void tapText(String text) {
        findByTextScrolling(text).click();
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitForGone(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ------------------------------------------------------------- queries --

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** True when the element with this text is on screen, scrolling to look for it. */
    public boolean isTextVisible(String text) {
        try {
            return findByTextScrolling(text).isDisplayed();
        } catch (NoSuchElementException absent) {
            return false;
        }
    }

    public void navigateBack() {
        driver.navigate().back();
    }

    // --------------------------------------------------------- diagnostics --

    private static final Pattern TEXT_ATTRIBUTE = Pattern.compile("text=\"([^\"]+)\"");

    /**
     * Every distinct non-empty text on the current screen, read out of the UI
     * hierarchy in a single call. Used to make failure messages say what the
     * device was really showing instead of just what was missing.
     */
    public List<String> visibleTexts() {
        List<String> texts = new ArrayList<>();
        Matcher m = TEXT_ATTRIBUTE.matcher(driver.getPageSource());
        while (m.find()) {
            String text = m.group(1);
            if (!text.isBlank() && !texts.contains(text)) {
                texts.add(text);
            }
        }
        return texts;
    }

    /** The package name of the app currently in the foreground, or "?" . */
    public String foregroundPackage() {
        Matcher m = Pattern.compile("package=\"([^\"]+)\"").matcher(driver.getPageSource());
        return m.find() ? m.group(1) : "?";
    }
}
