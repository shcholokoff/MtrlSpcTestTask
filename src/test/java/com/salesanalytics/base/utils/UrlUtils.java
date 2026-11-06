package com.salesanalytics.base.utils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class UrlUtils {

	private UrlUtils() {
	}

	public static Map<String, String> getQueryParams(final String url) {
		final String query = URI.create(url).getRawQuery();
		final Map<String, String> params = new LinkedHashMap<>();
		if (query == null || query.isEmpty()) {
			return Collections.unmodifiableMap(params);
		}
		for (final String pair : query.split("&")) {
			final String[] nameAndValue = pair.split("=", 2);
			params.put(decode(nameAndValue[0]), nameAndValue.length > 1 ? decode(nameAndValue[1]) : "");
		}
		return Collections.unmodifiableMap(params);
	}

	private static String decode(final String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}
}
