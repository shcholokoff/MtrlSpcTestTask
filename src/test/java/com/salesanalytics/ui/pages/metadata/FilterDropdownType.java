package com.salesanalytics.ui.pages.metadata;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.salesanalytics.base.references.UIDictionary;

public enum FilterDropdownType implements UIDictionary {

	DATE_RANGE("Date range", true),
	REGION("Region", true),
	CATEGORY("Category", true),
	SORT_BY("Sort by", false);

	private final String label;

	private final boolean isFilter;

	FilterDropdownType(final String label, final boolean isFilter) {
		this.label = label;
		this.isFilter = isFilter;
	}

	public boolean isFilter() {
		return isFilter;
	}

	public static List<FilterDropdownType> getFilters() {
		return Arrays.stream(values())
					 .filter(FilterDropdownType::isFilter)
					 .collect(Collectors.toList());
	}

	public static FilterDropdownType fromLabel(final String label) {
		for (final FilterDropdownType dropdown : values()) {
			if (dropdown.label.equals(label)) return dropdown;
		}
		throw new IllegalArgumentException("No enum constant " + FilterDropdownType.class.getCanonicalName()
				+ " with label \"" + label + "\"");
	}

	@Override
	public String getLabel() {
		return this.label;
	}
}
