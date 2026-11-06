package com.salesanalytics.ui.steps;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.salesanalytics.ui.pages.metadata.FilterDropdownType.SORT_BY;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.salesanalytics.base.core.PlaywrightManager;
import com.salesanalytics.base.core.TestProperties;
import com.salesanalytics.base.references.SortType;
import com.salesanalytics.base.rest.transactions.dto.TransactionVO;
import com.salesanalytics.ui.pages.analytics.SalesAnalyticsPage;
import com.salesanalytics.ui.pages.fragments.FiltersFragment;
import com.salesanalytics.ui.pages.fragments.TransactionsTableFragment;
import com.salesanalytics.ui.pages.metadata.FilterDropdownType;
import org.assertj.core.api.Assertions;

public class TransactionsSteps extends UIGeneralSteps {

	private SalesAnalyticsPage salesAnalyticsPage;

	public TransactionsSteps(final PlaywrightManager playwrightManager, final TestProperties properties) {
		super(playwrightManager, properties);
	}

	public void openTransactionsPage() {
		salesAnalyticsPage = openSalesAnalyticsPage();
	}

	public void applySelection(final Map<String, String> selection) {
		logger.info("Apply selection {}", selection);
		final FiltersFragment filtersFragment = getSalesAnalyticsPage().getFiltersFragment();
		selection.forEach((dropdown, option) ->
				filtersFragment.selectOptionInDropdown(FilterDropdownType.fromLabel(dropdown), option));
		getSalesAnalyticsPage().applyFilters();
	}

	public void clearFilters() {
		logger.info("Clear filters");
		getSalesAnalyticsPage().clearFilters();
	}

	public void assertOrderIdsInTable(final List<String> expectedOrderIds) {
		logger.info("Assert order IDs in the table are {}", expectedOrderIds);
		final Locator dataRows = getTransactionsTable().getDataRows();
		assertThat(dataRows).hasCount(expectedOrderIds.size());
		assertThat(dataRows).containsText(expectedOrderIds.toArray(String[]::new));
	}

	public void assertCounterText(final String expectedText) {
		logger.info("Assert transactions counter is [{}]", expectedText);
		assertThat(getTransactionsTable().getCounterLabel()).hasText(expectedText);
	}

	public void assertAllFiltersReset(final String expectedOption) {
		logger.info("Assert all filters are reset to [{}]", expectedOption);
		final FiltersFragment filtersFragment = getSalesAnalyticsPage().getFiltersFragment();
		FilterDropdownType.getFilters().forEach(filter ->
				assertThat(filtersFragment.getSelectedOption(filter)).hasText(expectedOption));
	}

	public void assertSelectedSortType(final String expectedSortType) {
		logger.info("Assert selected sorting is [{}]", expectedSortType);
		assertThat(getSalesAnalyticsPage().getFiltersFragment().getSelectedOption(SORT_BY)).hasText(expectedSortType);
	}

	public void assertTransactionsSortedBy(final int expectedCount, final String sortLabel) {
		final SortType sortType = SortType.fromLabel(sortLabel);
		logger.info("Assert table shows [{}] transactions sorted by [{}]", expectedCount, sortLabel);
		final List<TransactionVO> sortedApiTransactions = getSalesAnalyticsPage().getLastTransactionsFromApi()
																			   .stream()
																			   .sorted(sortType.getComparator())
																			   .collect(Collectors.toList());
		assertThat(getTransactionsTable().getDataRows()).hasCount(expectedCount);
		assertTableShows(sortedApiTransactions);
	}

	public void assertMessageDisplayed(final String expectedMessage) {
		logger.info("Assert [{}] message is displayed", expectedMessage);
		final TransactionsTableFragment transactionsTable = getTransactionsTable();
		assertThat(transactionsTable.getMessage(expectedMessage)).isVisible();
		assertThat(transactionsTable.getDataRows()).hasCount(0);
	}

	public void assertTableMatchesApiResponse() {
		final List<TransactionVO> apiTransactions = getSalesAnalyticsPage().getLastTransactionsFromApi();
		logger.info("Assert table shows exactly the [{}] transactions returned by the API", apiTransactions.size());
		assertTableShows(apiTransactions);
	}

	public void assertDropdownOptions(final Map<String, String> optionsByDropdown) {
		logger.info("Assert dropdowns contain exactly the options {}", optionsByDropdown);
		final FiltersFragment filtersFragment = getSalesAnalyticsPage().getFiltersFragment();
		final Map<FilterDropdownType, String[]> expectedOptions = new LinkedHashMap<>();
		optionsByDropdown.forEach((dropdown, options) -> expectedOptions.put(FilterDropdownType.fromLabel(dropdown),
				Arrays.stream(options.split(",")).map(String::trim).toArray(String[]::new)));
		Assertions.assertThat(expectedOptions.keySet())
				  .as("Dropdowns with listed options")
				  .containsExactlyInAnyOrder(FilterDropdownType.values());
		expectedOptions.forEach((dropdown, options) -> assertThat(filtersFragment.getOptions(dropdown)).hasText(options));
	}

	public void assertLastRequestParams(final Map<String, String> expectedParams) {
		final Map<String, String> requestParams = getSalesAnalyticsPage().getLastTransactionsRequestParams();
		logger.info("Assert last transactions request params {} are {}", requestParams, expectedParams);
		Assertions.assertThat(requestParams)
				  .as("Params of the last transactions request")
				  .isEqualTo(expectedParams);
	}

	private void assertTableShows(final List<TransactionVO> expectedTransactions) {
		final TransactionsTableFragment transactionsTable = getTransactionsTable();
		if (expectedTransactions.isEmpty()) {
			assertThat(transactionsTable.getDataRows()).hasCount(0);
			return;
		}
		assertThat(transactionsTable.getDataRows()).hasText(transactionsTable.getRowTexts(expectedTransactions),
				new LocatorAssertions.HasTextOptions().setUseInnerText(true));
	}

	private TransactionsTableFragment getTransactionsTable() {
		return getSalesAnalyticsPage().getTransactionsTable();
	}

	private SalesAnalyticsPage getSalesAnalyticsPage() {
		if (salesAnalyticsPage == null) {
			throw new IllegalStateException("Sales Analytics page is not opened");
		}
		return salesAnalyticsPage;
	}
}
