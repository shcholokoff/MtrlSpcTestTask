package com.salesanalytics.base.rest.transactions.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionsQueryVO {

	public static final String DATE_RANGE_PARAM = "dateRange";
	public static final String REGION_PARAM = "region";
	public static final String CATEGORY_PARAM = "category";
	public static final String SORT_PARAM = "sort";

	private static final List<String> SUPPORTED_PARAMS = List.of(DATE_RANGE_PARAM, REGION_PARAM, CATEGORY_PARAM, SORT_PARAM);

	private String dateRange;

	private String region;

	private String category;

	private String sort;

	public static TransactionsQueryVO fromParams(final Map<String, String> params) {
		params.keySet().forEach(name -> {
			if (!SUPPORTED_PARAMS.contains(name)) {
				throw new IllegalArgumentException(String.format("Unsupported param [%s], supported: %s", name, SUPPORTED_PARAMS));
			}
		});
		return builder().withDateRange(params.get(DATE_RANGE_PARAM))
						.withRegion(params.get(REGION_PARAM))
						.withCategory(params.get(CATEGORY_PARAM))
						.withSort(params.get(SORT_PARAM))
						.build();
	}

	public Map<String, String> toQueryParams() {
		final Map<String, String> params = new LinkedHashMap<>();
		putIfPresent(params, DATE_RANGE_PARAM, dateRange);
		putIfPresent(params, REGION_PARAM, region);
		putIfPresent(params, CATEGORY_PARAM, category);
		putIfPresent(params, SORT_PARAM, sort);
		return params;
	}

	private static void putIfPresent(final Map<String, String> params, final String name, final String value) {
		if (value != null) {
			params.put(name, value);
		}
	}
}
