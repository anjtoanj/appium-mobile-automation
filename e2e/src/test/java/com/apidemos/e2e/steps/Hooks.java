package com.apidemos.e2e.steps;

import com.apidemos.e2e.config.Config;
import com.apidemos.e2e.core.AppLauncher;
import com.apidemos.e2e.core.Diagnostics;
import com.apidemos.e2e.core.DriverFactory;
import com.apidemos.e2e.core.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;

/**
 * One Appium session per scenario, so a scenario can never inherit UI state
 * left behind by the one before it.
 *
 * @After hooks run highest-order-first, so the screenshot is captured while the
 * session is still alive and the session is torn down afterwards.
 */
public class Hooks {

    private int stepNumber = 0;

    /**
     * A breakpoint anchor.
     *
     * Cucumber cannot step through a .feature file the way a debugger steps
     * through code, because Gherkin steps are not statements. Putting a
     * breakpoint on the counter line below is the equivalent: the debugger
     * pauses before EVERY step of every scenario, with the app frozen mid-flow
     * on the emulator so you can look at it.
     *
     * From there, Step Into drops you into that step's definition.
     */
    @BeforeStep
    public void beforeEachStep(Scenario scenario) {
        stepNumber++;   // <-- BREAKPOINT HERE to pause before each Gherkin step
        if (Config.getBoolean("log.steps", false)) {
            System.out.println("[step " + stepNumber + "] " + scenario.getName());
        }
    }

    @Before(order = 0)
    public void startSession() {
        DriverManager.set(DriverFactory.create());
        // Do not trust the driver's implicit launch - put the app in the
        // foreground ourselves, from a cold start.
        AppLauncher.restart(DriverManager.get(), Config.require("app.package"));
    }

    @After(order = 100)
    public void captureScreenshotOnFailure(Scenario scenario) {
        if (!scenario.isFailed() || !DriverManager.hasDriver()) {
            return;
        }
        Diagnostics.dump(scenario.getName());
        try {
            byte[] png = DriverManager.get().getScreenshotAs(OutputType.BYTES);
            scenario.attach(png, "image/png", scenario.getName());
        } catch (RuntimeException screenshotFailed) {
            // A dead session must not mask the real failure.
            scenario.log("Could not capture a screenshot: " + screenshotFailed.getMessage());
        }
    }

    /**
     * Runs last (lowest @After order). Closes the app before dropping the
     * session so the emulator is left on the launcher rather than sitting in
     * the app - the driver will not do it for us while noReset is true.
     *
     * Set close.app.after.scenario = false when you want the app left on screen
     * to poke at after a failure.
     */
    @After(order = 0)
    public void endSession() {
        if (DriverManager.hasDriver() && Config.getBoolean("close.app.after.scenario", true)) {
            try {
                AppLauncher.close(DriverManager.get(), Config.require("app.package"));
            } catch (RuntimeException couldNotClose) {
                // A failed close must never mask the scenario's own failure.
                System.err.println("Hooks: could not close the app - " + couldNotClose.getMessage());
            }
        }
        DriverManager.quit();
    }
}
