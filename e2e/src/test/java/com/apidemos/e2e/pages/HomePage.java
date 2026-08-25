package com.apidemos.e2e.pages;

import com.apidemos.e2e.core.Diagnostics;
import org.openqa.selenium.TimeoutException;

import java.util.List;

/**
 * The ApiDemos launch screen. The category list below is taken from the app's
 * own AndroidManifest.xml: every activity registered under the SAMPLE_CODE
 * intent category, grouped by the first segment of its label.
 */
public class HomePage extends SampleListPage {

    public static final List<String> CATEGORIES = List.of(
            "Accessibility",
            "Animation",
            "App",
            "Content",
            "Graphics",
            "Media",
            "NFC",
            "OS",
            "Preference",
            "Text",
            "Views");

    public SampleListPage openCategory(String category) {
        return open(category);
    }

    /**
     * Blocks until the home list is actually on screen. The app takes a moment
     * to draw after being activated, so a single check races the launch.
     */
    public void waitUntilLoaded() {
        try {
            waitForVisible(byText("Views"));
        } catch (TimeoutException neverAppeared) {
            Diagnostics.dump("home-screen-check");
            throw new AssertionError(
                    "The ApiDemos home screen did not load - 'Views' never appeared."
                            + System.lineSeparator() + "  Foreground package: " + foregroundPackage()
                            + System.lineSeparator() + "  Text on screen: " + visibleTexts()
                            + System.lineSeparator() + "  UI hierarchy and screenshot: target/diagnostics/");
        }
    }
}
