package com.apidemos.e2e.steps;

import com.apidemos.e2e.pages.AlertDialogsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/** Steps for App / Alert Dialogs. */
public class AlertDialogSteps {

    private AlertDialogsPage page;

    @When("I tap the {string} row")
    public void iTapTheRow(String rowLabel) {
        page().openDialog(rowLabel);
    }

    @Then("a dialog is displayed")
    public void aDialogIsDisplayed() {
        Assert.assertTrue(page().isDialogDisplayed(), "No dialog appeared.");
    }

    @Then("no dialog is displayed")
    public void noDialogIsDisplayed() {
        Assert.assertFalse(page().isDialogDisplayed(), "A dialog was still on screen.");
    }

    @Then("the dialog positive button reads {string}")
    public void theDialogPositiveButtonReads(String expected) {
        Assert.assertEquals(page().positiveButtonLabel().trim(), expected,
                "Unexpected label on the dialog's positive button.");
    }

    @Then("the dialog negative button reads {string}")
    public void theDialogNegativeButtonReads(String expected) {
        Assert.assertEquals(page().negativeButtonLabel().trim(), expected,
                "Unexpected label on the dialog's negative button.");
    }

    @When("I dismiss the dialog with the negative button")
    public void iDismissTheDialogWithTheNegativeButton() {
        page().tapNegative();
    }

    private AlertDialogsPage page() {
        if (page == null) {
            page = new AlertDialogsPage();
        }
        return page;
    }
}
