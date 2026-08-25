package com.apidemos.e2e.core;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Writes the current screen to target/diagnostics/ when something goes wrong:
 * the full UI hierarchy as XML plus a PNG. Between them you can always tell
 * what the device was actually showing, which is far quicker than re-running
 * with a debugger attached.
 */
public final class Diagnostics {

    private static final Path DIR = Paths.get("target", "diagnostics");

    private Diagnostics() {
    }

    public static void dump(String label) {
        if (!DriverManager.hasDriver()) {
            return;
        }
        AndroidDriver driver = DriverManager.get();
        String stem = label.replaceAll("[^a-zA-Z0-9-_]+", "_");

        try {
            Files.createDirectories(DIR);
        } catch (Exception cannotCreate) {
            System.err.println("Diagnostics: could not create " + DIR.toAbsolutePath()
                    + " - " + cannotCreate.getMessage());
            return;
        }

        try {
            Path xml = DIR.resolve(stem + "-page.xml");
            Files.write(xml, driver.getPageSource().getBytes(StandardCharsets.UTF_8));
            System.out.println("Diagnostics: UI hierarchy -> " + xml.toAbsolutePath());
        } catch (Exception noSource) {
            System.err.println("Diagnostics: could not capture the page source - " + noSource.getMessage());
        }

        try {
            Path png = DIR.resolve(stem + "-screen.png");
            Files.write(png, driver.getScreenshotAs(OutputType.BYTES));
            System.out.println("Diagnostics: screenshot   -> " + png.toAbsolutePath());
        } catch (Exception noShot) {
            System.err.println("Diagnostics: could not capture a screenshot - " + noShot.getMessage());
        }
    }
}
