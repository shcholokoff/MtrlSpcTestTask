package com.salesanalytics.base.references;

import java.time.LocalDate;
import java.util.Comparator;

import com.salesanalytics.base.rest.transactions.dto.TransactionVO;

public enum SortType implements UIDictionary {

	DATE_ASC("date_asc", "Date: Oldest → Newest", Comparator.comparing((TransactionVO t) -> LocalDate.parse(t.getDate()))),
	DATE_DESC("date_desc", "Date: Newest → Oldest", Comparator.comparing((TransactionVO t) -> LocalDate.parse(t.getDate())).reversed()),
	REVENUE_ASC("rev_asc", "Revenue: Low → High", Comparator.comparing(TransactionVO::getRevenue)),
	REVENUE_DESC("rev_desc", "Revenue: High → Low", Comparator.comparing(TransactionVO::getRevenue).reversed());

	private final String value;

	private final String label;

	private final Comparator<TransactionVO> comparator;

	SortType(final String value, final String label, final Comparator<TransactionVO> comparator) {
		this.value = value;
		this.label = label;
		this.comparator = comparator;
	}

	public String getValue() {
		return value;
	}

	public Comparator<TransactionVO> getComparator() {
		return comparator;
	}

	public static SortType getDefault() {
		return DATE_ASC;
	}

	public static SortType fromLabel(final String label) {
		for (final SortType sortType : values()) {
			if (sortType.label.equals(label)) return sortType;
		}
		throw new IllegalArgumentException("No enum constant " + SortType.class.getCanonicalName()
				+ " with label \"" + label + "\"");
	}

	@Override
	public String getLabel() {
		return this.label;
	}
}
