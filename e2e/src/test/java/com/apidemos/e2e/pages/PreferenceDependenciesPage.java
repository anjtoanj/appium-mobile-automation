package com.apidemos.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Preference / 3. Preference dependencies.
 *
 * A "WiFi" checkbox preference gates a "WiFi settings" edit-text preference:
 * the latter stays disabled until the former is ticked. Titles come from
 * strings.xml (title_wifi, title_wifi_settings).
 */
public class PreferenceDependenciesPage extends BasePage {

    public static final String WIFI = "WiFi";
    public static final String WIFI_SETTINGS = "WiFi settings";

    /**
     * A preference row is laid out as:
     *
     *   LinearLayout                      <- the row
     *     RelativeLayout
     *       TextView  android:id/title    <- the label we search by
     *     LinearLayout android:id/widget_frame
     *       CheckBox  android:id/checkbox <- what we want
     *
     * so "up two levels from the title, then down to the checkbox" pins the
     * checkbox to its own row.
     *
     * Deliberately NOT the `following::` axis: it is valid XPath 1.0, but the
     * uiautomator2 driver evaluates XPath with an XPath 2.0 engine that throws
     * "ArrayList$ListItr cannot be cast to ... NodeType" on that axis. Plain
     * parent/child steps avoid the bug entirely.
     */
    private static final String CHECKBOX_IN_ROW =
            "//android.widget.TextView[@text='%s']/../..//android.widget.CheckBox";

    private static final By DIALOG_INPUT_BY_ID = By.id("android:id/edit");
    private static final By ANY_EDIT_TEXT = By.className("android.widget.EditText");
    private static final By DIALOG_OK = By.id("android:id/button1");
    private static final By DIALOG_CANCEL = By.id("android:id/button2");

    // ------------------------------------------------------------ checkbox --

    public boolean isChecked(String preferenceTitle) {
        WebElement box = driver.findElement(By.xpath(String.format(CHECKBOX_IN_ROW, preferenceTitle)));
        return Boolean.parseBoolean(box.getAttribute("checked"));
    }

    /**
     * Puts the checkbox into the requested state, tapping only if it is not
     * already there. Blindly tapping makes the step depend on what the previous
     * scenario left behind - the preference is persisted by Android, so a tap
     * is a toggle, not a "set".
     */
    public void setChecked(String preferenceTitle, boolean desired) {
        if (isChecked(preferenceTitle) == desired) {
            return;
        }
        tapText(preferenceTitle);
        wait.until(ignored -> isChecked(preferenceTitle) == desired);
    }

    // ----------------------------------------------------------- enabled? --

    /**
     * Android disables a preference row's child views when its dependency is
     * unmet, so the title TextView reports enabled=false. Verified against a
     * real device: the "WiFi settings" title reads enabled=false until "WiFi"
     * is ticked.
     */
    public boolean isEnabled(String preferenceTitle) {
        WebElement title = findByTextScrolling(preferenceTitle);
        return Boolean.parseBoolean(title.getAttribute("enabled"));
    }

    public void openPreference(String preferenceTitle) {
        tapText(preferenceTitle);
    }

    // -------------------------------------------------------------- dialog --

    /**
     * The framework's EditTextPreference dialog normally uses android:id/edit,
     * but that id is not guaranteed across Android versions - fall back to the
     * only EditText in the dialog.
     */
    private WebElement dialogInput() {
        return wait.until(driver -> {
            List<WebElement> byId = driver.findElements(DIALOG_INPUT_BY_ID);
            if (!byId.isEmpty()) {
                return byId.get(0);
            }
            List<WebElement> anyInput = driver.findElements(ANY_EDIT_TEXT);
            return anyInput.isEmpty() ? null : anyInput.get(0);
        });
    }

    public void typeIntoDialog(String value) {
        WebElement input = dialogInput();
        input.clear();
        input.sendKeys(value);
    }

    public String dialogInputValue() {
        return dialogInput().getText();
    }

    public void confirmDialog() {
        waitForVisible(DIALOG_OK).click();
        waitForGone(DIALOG_OK);
    }

    public void cancelDialog() {
        waitForVisible(DIALOG_CANCEL).click();
        waitForGone(DIALOG_CANCEL);
    }
}
