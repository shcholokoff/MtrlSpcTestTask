package com.salesanalytics.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.restassured.response.ResponseBody;

public final class DeserializerUtils {

	private static final ObjectMapper MAPPER = JsonMapper.builder()
														 .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
														 .build();

	private DeserializerUtils() {
	}

	public static <T> T deserializeObjectFromResponse(final ResponseBody<?> responseBody, final Class<T> type) {
		return deserializeObject(responseBody.asString(), type);
	}

	public static <T> T deserializeObject(final String json, final Class<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (JsonProcessingException e) {
			throw new AssertionError(String.format("Response body can not be deserialized to [%s]: %s%nBody: %s",
					type.getSimpleName(), e.getOriginalMessage(), json), e);
		}
	}
}
