package com.salesanalytics.base.rest.transactions;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import com.salesanalytics.base.core.TestProperties;
import com.salesanalytics.base.rest.RequestConfigurator;
import com.salesanalytics.base.rest.transactions.dto.TransactionsQueryVO;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;

public class TransactionsService extends RequestConfigurator {

	public static final String TRANSACTIONS_PATH = "/api/transactions";
	public static final String TRANSACTIONS_RESPONSE_SCHEMA = "schemas/transactions-response.json";

	public TransactionsService(final TestProperties properties) {
		super(properties);
		this.basePath = TRANSACTIONS_PATH;
	}

	public Response getTransactions(final TransactionsQueryVO query) {
		return getRequestSpecificationBase().queryParams(query.toQueryParams())
											.get();
	}

	public ResponseSpecification getTransactionsResponseSpecification(final int expectedStatusCode) {
		return getResponseSpecificationBase(expectedStatusCode).expectBody(matchesJsonSchemaInClasspath(TRANSACTIONS_RESPONSE_SCHEMA))
															   .build();
	}
}
