package com.apidemos.e2e.steps;

import com.apidemos.e2e.pages.HomePage;
import com.apidemos.e2e.pages.SampleListPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/** Steps for moving around the ApiDemos list tree. */
public class NavigationSteps {

    private SampleListPage currentScreen;

    @Given("the ApiDemos app is open on the home screen")
    public void theAppIsOpenOnTheHomeScreen() {
        HomePage home = new HomePage();
        home.waitUntilLoaded();
        currentScreen = home;
    }

    @When("I open the {string} category")
    public void iOpenTheCategory(String category) {
        currentScreen = screen().open(category);
    }

    @When("I open the {string} demo")
    public void iOpenTheDemo(String demo) {
        currentScreen = screen().open(demo);
    }

    @Then("the list shows {string}")
    public void theListShows(String label) {
        Assert.assertTrue(screen().shows(label),
                "Expected the current list to show '" + label + "'.");
    }

    @Then("the home screen shows the categories:")
    public void theHomeScreenShowsTheCategories(DataTable categories) {
        SampleListPage screen = screen();
        List<String> missing = new ArrayList<>();
        for (String category : categories.asList()) {
            if (!screen.shows(category)) {
                missing.add(category);
            }
        }
        Assert.assertTrue(missing.isEmpty(),
                "These categories were not found on the home screen: " + missing);
    }

    @When("I navigate back")
    public void iNavigateBack() {
        screen().navigateBack();
        currentScreen = new SampleListPage();
    }

    private SampleListPage screen() {
        if (currentScreen == null) {
            currentScreen = new SampleListPage();
        }
        return currentScreen;
    }
}
