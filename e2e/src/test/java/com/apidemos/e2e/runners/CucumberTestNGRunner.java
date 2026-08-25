package com.apidemos.e2e.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG entry point for the Cucumber scenarios. Surefire runs this via
 * testng.xml.
 *
 * Filter with tags from the command line:
 *   mvn test -Dcucumber.filter.tags="@smoke"
 *
 * Set parallel = true on the data provider once the suite is stable and you
 * have more than one device available - each scenario gets its own session, so
 * the driver layer is already thread-safe.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.apidemos.e2e.steps"},
        plugin = {
                "pretty",
                "summary",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json"
        },
        monochrome = true
)
public class CucumberTestNGRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
