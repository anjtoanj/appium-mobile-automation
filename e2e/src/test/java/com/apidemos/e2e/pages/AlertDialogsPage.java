package com.apidemos.e2e.pages;

import org.openqa.selenium.By;

/**
 * App / Alert Dialogs. Row labels come from strings.xml
 * (e.g. alert_dialog_two_buttons = "OK Cancel dialog with a message").
 *
 * The dialog buttons use the framework ids rather than their labels, which
 * keeps the assertions stable across locales and Android versions.
 */
public class AlertDialogsPage extends BasePage {

    public static final String OK_CANCEL_WITH_MESSAGE = "OK Cancel dialog with a message";
    public static final String TEXT_ENTRY = "Text Entry dialog";

    private static final By POSITIVE_BUTTON = By.id("android:id/button1");
    private static final By NEGATIVE_BUTTON = By.id("android:id/button2");
    private static final By DIALOG_TITLE = By.id("android:id/alertTitle");

    public void openDialog(String rowLabel) {
        tapText(rowLabel);
    }

    public boolean isDialogDisplayed() {
        return isPresent(POSITIVE_BUTTON) || isPresent(DIALOG_TITLE);
    }

    public String positiveButtonLabel() {
        return waitForVisible(POSITIVE_BUTTON).getText();
    }

    public String negativeButtonLabel() {
        return waitForVisible(NEGATIVE_BUTTON).getText();
    }

    public void tapPositive() {
        waitForVisible(POSITIVE_BUTTON).click();
        waitForGone(POSITIVE_BUTTON);
    }

    public void tapNegative() {
        waitForVisible(NEGATIVE_BUTTON).click();
        waitForGone(NEGATIVE_BUTTON);
    }
}
