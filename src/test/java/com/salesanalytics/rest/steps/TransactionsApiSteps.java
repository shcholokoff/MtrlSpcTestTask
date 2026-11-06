package com.salesanalytics.rest.steps;

import static com.salesanalytics.base.core.parameters.Constants.NOT_SELECTED_FILTER_VALUE;
import static com.salesanalytics.base.rest.transactions.dto.TransactionsQueryVO.CATEGORY_PARAM;
import static com.salesanalytics.base.rest.transactions.dto.TransactionsQueryVO.DATE_RANGE_PARAM;
import static com.salesanalytics.base.rest.transactions.dto.TransactionsQueryVO.REGION_PARAM;
import static com.salesanalytics.base.rest.transactions.dto.TransactionsQueryVO.SORT_PARAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.salesanalytics.base.references.SortType;
import com.salesanalytics.base.rest.transactions.TransactionsService;
import com.salesanalytics.base.rest.transactions.dto.TransactionVO;
import com.salesanalytics.base.rest.transactions.dto.TransactionsQueryVO;
import com.salesanalytics.base.rest.transactions.dto.TransactionsResponse;
import com.salesanalytics.base.utils.DeserializerUtils;
import com.salesanalytics.rest.steps.context.TransactionsRequestResult;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.assertj.core.api.SoftAssertions;

public class TransactionsApiSteps extends APIGeneralSteps {

	private static final String ID_COLUMN = "id";
	private static final String DATE_COLUMN = "date";
	private static final String REGION_COLUMN = "region";
	private static final String CATEGORY_COLUMN = "category";
	private static final String REVENUE_COLUMN = "revenue";
	private static final String EXPECTED_ORDER_IDS_COLUMN = "expected order IDs";
	private static final String OMITTED_VALUE = "omitted";
	private static final String NO_DATA_VALUE = "no data";
	private static final Map<String, String> DEFAULT_PARAMS = Map.of(
			DATE_RANGE_PARAM, NOT_SELECTED_FILTER_VALUE,
			REGION_PARAM, NOT_SELECTED_FILTER_VALUE,
			CATEGORY_PARAM, NOT_SELECTED_FILTER_VALUE,
			SORT_PARAM, SortType.getDefault().getValue());

	private final TransactionsService transactionsService;
	private final List<TransactionsRequestResult> requestResults = new ArrayList<>();
	private Map<String, TransactionVO> applicationTransactionsById;

	public TransactionsApiSteps(final TransactionsService transactionsService) {
		this.transactionsService = transactionsService;
	}

	public void setApplicationTransactions(final List<Map<String, String>> transactions) {
		applicationTransactionsById = transactions.stream()
												  .map(TransactionsApiSteps::toTransaction)
												  .collect(Collectors.toMap(TransactionVO::getId,
														  Function.identity(),
														  (first, duplicate) -> {
															  throw new IllegalArgumentException(
																  String.format("Duplicate transaction [%s]", first.getId()));
														  },
														  LinkedHashMap::new));
		logger.info("Application contains [{}] transactions", applicationTransactionsById.size());
	}

	public void getTransactions(final List<Map<String, String>> requests) {
		logger.info("Get transactions for [{}] requests", requests.size());
		for (final Map<String, String> request : requests) {
			final Map<String, String> params = new LinkedHashMap<>(request);
			final List<String> expectedOrderIds = parseOrderIds(params.remove(EXPECTED_ORDER_IDS_COLUMN));
			params.values().removeIf(OMITTED_VALUE::equals);
			final TransactionsQueryVO query = TransactionsQueryVO.fromParams(params);
			logger.info("Get transactions with params {}", query.toQueryParams());
			final Response response = transactionsService.getTransactions(query);
			requestResults.add(new TransactionsRequestResult(query, expectedOrderIds, response));
		}
	}

	public void assertAllResponsesMatchContract(final int expectedStatusCode) {
		logger.info("Assert all responses have status [{}] and match transactions schema", expectedStatusCode);
		final ResponseSpecification responseSpecification = transactionsService.getTransactionsResponseSpecification(expectedStatusCode);
		final SoftAssertions sa = new SoftAssertions();
		for (final TransactionsRequestResult result : getRequestResults()) {
			final Map<String, String> params = result.getQuery().toQueryParams();
			final Response response = result.getResponse();
			sa.assertThatCode(() -> response.then().spec(responseSpecification))
			  .as("Contract of response for %s", params)
			  .doesNotThrowAnyException();
			if (!sa.wasSuccess()) continue;
			final TransactionsResponse body = getTransactionsResponse(response);
			final Map<String, String> expectedFilters = new LinkedHashMap<>(DEFAULT_PARAMS);
			expectedFilters.putAll(params);
			sa.assertThat(body.getMeta().getTotal()).as("meta.total for %s", params).isEqualTo(getApplicationTransactionsById().size());
			sa.assertThat(body.getMeta().getReturned()).as("meta.returned for %s", params).isEqualTo(body.getData().size());
			sa.assertThat(body.getMeta().getFilters()).as("meta.filters for %s", params).containsAllEntriesOf(expectedFilters);
		}
		sa.assertAll();
	}

	public void assertAllResponsesContainExpectedTransactions() {
		logger.info("Assert all responses contain exactly the expected transactions field by field in the expected order");
		final SoftAssertions sa = new SoftAssertions();
		for (final TransactionsRequestResult result : getRequestResults()) {
			sa.assertThat(getTransactionsResponse(result.getResponse()).getData())
			  .as("Transactions for %s", result.getQuery().toQueryParams())
			  .usingRecursiveComparison()
			  .isEqualTo(getApplicationTransactions(result.getExpectedOrderIds()));
		}
		sa.assertAll();
	}

	private static List<String> parseOrderIds(final String orderIds) {
		if (orderIds == null) {
			throw new IllegalArgumentException(String.format("Column [%s] is required", EXPECTED_ORDER_IDS_COLUMN));
		}
		if (NO_DATA_VALUE.equals(orderIds)) {
			return List.of();
		}
		return Arrays.stream(orderIds.split(","))
					 .map(String::trim)
					 .collect(Collectors.toList());
	}

	private static TransactionVO toTransaction(final Map<String, String> row) {
		return TransactionVO.builder()
							.withId(row.get(ID_COLUMN))
							.withDate(row.get(DATE_COLUMN))
							.withRegion(row.get(REGION_COLUMN))
							.withCategory(row.get(CATEGORY_COLUMN))
							.withRevenue(Integer.valueOf(row.get(REVENUE_COLUMN)))
							.build();
	}

	private static TransactionsResponse getTransactionsResponse(final Response response) {
		return DeserializerUtils.deserializeObjectFromResponse(response, TransactionsResponse.class);
	}

	private List<TransactionVO> getApplicationTransactions(final List<String> orderIds) {
		final Map<String, TransactionVO> transactionsById = getApplicationTransactionsById();
		return orderIds.stream()
					   .map(id -> {
						   final TransactionVO transaction = transactionsById.get(id);
						   if (transaction == null) {
							   throw new IllegalArgumentException(String.format("Transaction [%s] is not in the application transactions", id));
						   }
						   return transaction;
					   })
					   .collect(Collectors.toList());
	}

	private List<TransactionsRequestResult> getRequestResults() {
		if (requestResults.isEmpty()) {
			throw new IllegalStateException("No requests have been sent in this scenario yet");
		}
		return requestResults;
	}

	private Map<String, TransactionVO> getApplicationTransactionsById() {
		if (applicationTransactionsById == null) {
			throw new IllegalStateException("Application transactions are not set in this scenario");
		}
		return applicationTransactionsById;
	}
}
