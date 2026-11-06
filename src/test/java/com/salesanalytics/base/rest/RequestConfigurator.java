package com.salesanalytics.base.rest;

import static com.salesanalytics.base.core.parameters.PropertyNames.DOMAIN_URL;
import static com.salesanalytics.base.core.parameters.PropertyNames.TIMEOUT_MS;
import static io.restassured.RestAssured.given;

import com.salesanalytics.base.core.TestProperties;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class RequestConfigurator {

	private static final String CONNECTION_TIMEOUT_PARAM = "http.connection.timeout";
	private static final String SOCKET_TIMEOUT_PARAM = "http.socket.timeout";

	private final TestProperties properties;

	protected String basePath = "";

	protected RequestConfigurator(final TestProperties properties) {
		this.properties = properties;
	}

	protected String getBaseUri() {
		return properties.getServerProperty(DOMAIN_URL);
	}

	protected String getBasePath() {
		return this.basePath;
	}

	protected TestProperties getProperties() {
		return this.properties;
	}

	protected RequestSpecification getRequestSpecificationBase() {
		return given().filter(new AllureRestAssured())
					  .config(getRestAssuredConfig())
					  .baseUri(getBaseUri())
					  .basePath(getBasePath())
					  .accept(ContentType.JSON);
	}

	protected ResponseSpecBuilder getResponseSpecificationBase(final int expectedStatusCode) {
		return new ResponseSpecBuilder().expectStatusCode(expectedStatusCode)
										.expectContentType(ContentType.JSON);
	}

	private RestAssuredConfig getRestAssuredConfig() {
		final int timeout = Integer.parseInt(properties.getCommonProperty(TIMEOUT_MS));
		return RestAssuredConfig.config()
								.httpClient(HttpClientConfig.httpClientConfig()
															.setParam(CONNECTION_TIMEOUT_PARAM, timeout)
															.setParam(SOCKET_TIMEOUT_PARAM, timeout));
	}
}
