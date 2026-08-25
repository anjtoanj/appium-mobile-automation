package com.apidemos.e2e.pages;

/**
 * ApiDemos is one activity that re-renders itself as a list at every level, so
 * the home screen and each category screen share this page object. Sub-screens
 * show only the trailing part of the label: the manifest entry
 * "Preference/3. Preference dependencies" appears as "3. Preference dependencies".
 */
public class SampleListPage extends BasePage {

    /** Taps a row and returns the list page for the screen it opened. */
    public SampleListPage open(String label) {
        tapText(label);
        return new SampleListPage();
    }

    public boolean shows(String label) {
        return isTextVisible(label);
    }
}
