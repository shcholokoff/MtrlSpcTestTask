package com.salesanalytics.ui.pages.analytics;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.List;
import java.util.Map;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.salesanalytics.base.rest.transactions.TransactionsService;
import com.salesanalytics.base.rest.transactions.dto.TransactionVO;
import com.salesanalytics.base.rest.transactions.dto.TransactionsResponse;
import com.salesanalytics.base.utils.DeserializerUtils;
import com.salesanalytics.base.utils.UrlUtils;
import com.salesanalytics.ui.pages.abstracts.AbstractPage;
import com.salesanalytics.ui.pages.fragments.FiltersFragment;
import com.salesanalytics.ui.pages.fragments.TransactionsTableFragment;
import org.slf4j.Logger;

public class SalesAnalyticsPage extends AbstractPage {

	private static final String LOADING_MESSAGE = "Loading transactions…";

	private final Locator loadingMessage;
	private final FiltersFragment filtersFragment;
	private final TransactionsTableFragment transactionsTable;
	private String lastTransactionsRequestUrl;
	private String lastTransactionsResponseBody;

	public SalesAnalyticsPage(final Page page, final Logger logger, final String pageUrl) {
		super(page, logger, pageUrl);
		this.loadingMessage = page.getByText(LOADING_MESSAGE, new Page.GetByTextOptions().setExact(true));
		this.filtersFragment = new FiltersFragment(page, logger);
		this.transactionsTable = new TransactionsTableFragment(page, logger);
	}

	public SalesAnalyticsPage openPage() {
		waitForTransactionsResponse(this::navigate);
		return this;
	}

	public SalesAnalyticsPage applyFilters() {
		waitForTransactionsResponse(filtersFragment::clickOnApplyButton);
		return this;
	}

	public SalesAnalyticsPage clearFilters() {
		waitForTransactionsResponse(filtersFragment::clickOnClearButton);
		return this;
	}

	public FiltersFragment getFiltersFragment() {
		return filtersFragment;
	}

	public TransactionsTableFragment getTransactionsTable() {
		return transactionsTable;
	}

	public Map<String, String> getLastTransactionsRequestParams() {
		if (lastTransactionsRequestUrl == null) {
			throw new IllegalStateException("The page has not requested transactions yet");
		}
		return UrlUtils.getQueryParams(lastTransactionsRequestUrl);
	}

	public List<TransactionVO> getLastTransactionsFromApi() {
		if (lastTransactionsResponseBody == null) {
			throw new IllegalStateException("The page has not received transactions yet");
		}
		return DeserializerUtils.deserializeObject(lastTransactionsResponseBody, TransactionsResponse.class).getData();
	}

	private void waitForTransactionsResponse(final Runnable action) {
		final Response response = page.waitForResponse(
				r -> r.url().contains(TransactionsService.TRANSACTIONS_PATH) && "GET".equals(r.request().method()),
				action);
		if (!response.ok()) {
			throw new IllegalStateException(String.format("Transactions request [%s] failed with status [%s]: %s",
					response.url(), response.status(), response.text()));
		}
		lastTransactionsRequestUrl = response.url();
		lastTransactionsResponseBody = response.text();
		logger.info("Transactions requested by [{}]", lastTransactionsRequestUrl);
		waitUntilTransactionsAreLoaded();
	}

	private void waitUntilTransactionsAreLoaded() {
		assertThat(loadingMessage).hasCount(0);
	}
}
