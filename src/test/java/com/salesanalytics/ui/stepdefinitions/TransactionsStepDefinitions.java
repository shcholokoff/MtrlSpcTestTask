package com.salesanalytics.ui.stepdefinitions;

import java.util.List;
import java.util.Map;

import com.salesanalytics.ui.steps.TransactionsSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TransactionsStepDefinitions {

	private final TransactionsSteps steps;

	public TransactionsStepDefinitions(final TransactionsSteps steps) {
		this.steps = steps;
	}

	@Given("the user opens the Sales Analytics page")
	public void openSalesAnalyticsPage() {
		steps.openTransactionsPage();
	}

	@When("the user applies the following selection:")
	public void applySelection(final Map<String, String> selection) {
		steps.applySelection(selection);
	}

	@When("the user clears the filters")
	public void clearFilters() {
		steps.clearFilters();
	}

	@Then("the table shows order IDs:")
	public void assertOrderIdsInTable(final List<String> expectedOrderIds) {
		steps.assertOrderIdsInTable(expectedOrderIds);
	}

	@Then("the transaction counter shows {string}")
	public void assertCounterText(final String expectedText) {
		steps.assertCounterText(expectedText);
	}

	@Then("all filters are reset to {string}")
	public void assertAllFiltersReset(final String expectedOption) {
		steps.assertAllFiltersReset(expectedOption);
	}

	@Then("the sorting is {string}")
	public void assertSelectedSortType(final String expectedSortType) {
		steps.assertSelectedSortType(expectedSortType);
	}

	@Then("the table shows {int} transactions sorted by {string}")
	public void assertTransactionsSortedBy(final int expectedCount, final String sortLabel) {
		steps.assertTransactionsSortedBy(expectedCount, sortLabel);
	}

	@Then("the table shows exactly the transactions returned by the API")
	public void assertTableMatchesApiResponse() {
		steps.assertTableMatchesApiResponse();
	}

	@Then("the dropdowns contain exactly the following options:")
	public void assertDropdownOptions(final Map<String, String> optionsByDropdown) {
		steps.assertDropdownOptions(optionsByDropdown);
	}

	@Then("the {string} message is displayed")
	public void assertMessageDisplayed(final String expectedMessage) {
		steps.assertMessageDisplayed(expectedMessage);
	}

	@Then("the table data was requested with:")
	public void assertLastRequestParams(final Map<String, String> expectedParams) {
		steps.assertLastRequestParams(expectedParams);
	}
}
