package com.apidemos.e2e.steps;

import com.apidemos.e2e.pages.PreferenceDependenciesPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/** Steps for Preference / 3. Preference dependencies. */
public class PreferenceSteps {

    private PreferenceDependenciesPage page;

    @When("I tick the {string} checkbox")
    public void iTickTheCheckbox(String title) {
        // "Tick" means "end up ticked", not "tap once" - the preference is
        // persisted, so a tap is a toggle.
        page().setChecked(title, true);
    }

    @When("I untick the {string} checkbox")
    public void iUntickTheCheckbox(String title) {
        page().setChecked(title, false);
    }

    @Then("the {string} checkbox is ticked")
    public void theCheckboxIsTicked(String title) {
        Assert.assertTrue(page().isChecked(title),
                "Expected the '" + title + "' checkbox to be ticked.");
    }

    @When("I open the {string} preference")
    public void iOpenThePreference(String title) {
        page().openPreference(title);
    }

    @When("I enter {string} into the dialog and confirm")
    public void iEnterIntoTheDialogAndConfirm(String value) {
        page().typeIntoDialog(value);
        page().confirmDialog();
    }

    @Then("the {string} preference is disabled")
    public void thePreferenceIsDisabled(String title) {
        Assert.assertFalse(page().isEnabled(title),
                "Expected the '" + title + "' preference to be disabled.");
    }

    @Then("the {string} preference is enabled")
    public void thePreferenceIsEnabled(String title) {
        Assert.assertTrue(page().isEnabled(title),
                "Expected the '" + title + "' preference to be enabled.");
    }

    @Then("the dialog input contains {string}")
    public void theDialogInputContains(String expected) {
        Assert.assertEquals(page().dialogInputValue(), expected,
                "The dialog did not keep the value that was entered.");
    }

    private PreferenceDependenciesPage page() {
        if (page == null) {
            page = new PreferenceDependenciesPage();
        }
        return page;
    }
}
