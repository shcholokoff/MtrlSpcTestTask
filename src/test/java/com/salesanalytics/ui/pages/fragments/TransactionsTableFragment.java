package com.salesanalytics.ui.pages.fragments;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.salesanalytics.base.rest.transactions.dto.TransactionVO;
import com.salesanalytics.ui.pages.abstracts.AbstractComponent;
import org.slf4j.Logger;

public class TransactionsTableFragment extends AbstractComponent {

	private static final String TABLE_NAME = "Sales transactions";
	private static final Pattern COUNTER_PATTERN = Pattern.compile("^Showing \\d+ of \\d+ transactions$");
	private static final String DATE_COLUMN = "Date";
	private static final String REGION_COLUMN = "Region";
	private static final String CATEGORY_COLUMN = "Category";
	private static final String REVENUE_COLUMN = "Revenue (USD)";
	private static final String ORDER_ID_COLUMN = "Order ID";

	private final Locator columnHeaders;
	private final Locator dataRows;
	private final Locator counterLabel;

	public TransactionsTableFragment(final Page page, final Logger logger) {
		super(page, logger);
		final Locator table = page.getByRole(AriaRole.TABLE, new Page.GetByRoleOptions().setName(TABLE_NAME).setExact(true));
		this.columnHeaders = table.getByRole(AriaRole.COLUMNHEADER);
		this.dataRows = table.getByRole(AriaRole.ROW).filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.CELL)));
		this.counterLabel = page.getByText(COUNTER_PATTERN);
	}

	public Locator getDataRows() {
		return dataRows;
	}

	public Locator getCounterLabel() {
		return counterLabel;
	}

	public Locator getMessage(final String text) {
		return page.getByText(text, new Page.GetByTextOptions().setExact(true));
	}

	public String[] getRowTexts(final List<TransactionVO> transactions) {
		final List<String> columns = columnHeaders.allInnerTexts()
												  .stream()
												  .map(String::trim)
												  .collect(Collectors.toList());
		return transactions.stream()
						   .map(transaction -> columns.stream()
													  .map(column -> getCellText(transaction, column))
													  .collect(Collectors.joining(" ")))
						   .toArray(String[]::new);
	}

	private static String getCellText(final TransactionVO transaction, final String column) {
		return switch (column) {
			case DATE_COLUMN -> transaction.getDate();
			case REGION_COLUMN -> transaction.getRegion();
			case CATEGORY_COLUMN -> transaction.getCategory();
			case REVENUE_COLUMN -> String.format(Locale.US, "%,d", transaction.getRevenue());
			case ORDER_ID_COLUMN -> transaction.getId();
			default -> throw new IllegalStateException(String.format("Unknown column [%s] in \"%s\" table", column, TABLE_NAME));
		};
	}
}
