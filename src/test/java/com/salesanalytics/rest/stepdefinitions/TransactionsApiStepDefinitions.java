package com.salesanalytics.rest.stepdefinitions;

import java.util.List;
import java.util.Map;

import com.salesanalytics.rest.steps.TransactionsApiSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TransactionsApiStepDefinitions {

	private final TransactionsApiSteps steps;

	public TransactionsApiStepDefinitions(final TransactionsApiSteps steps) {
		this.steps = steps;
	}

	@Given("the application contains the following transactions:")
	public void setApplicationTransactions(final List<Map<String, String>> transactions) {
		steps.setApplicationTransactions(transactions);
	}

	@When("the client requests transactions with:")
	public void getTransactions(final List<Map<String, String>> requests) {
		steps.getTransactions(requests);
	}

	@Then("every response has status {int} and matches the transactions schema")
	public void assertAllResponsesMatchContract(final int expectedStatusCode) {
		steps.assertAllResponsesMatchContract(expectedStatusCode);
	}

	@Then("every response contains exactly the expected transactions with all fields in the expected order")
	public void assertAllResponsesContainExpectedTransactions() {
		steps.assertAllResponsesContainExpectedTransactions();
	}
}
