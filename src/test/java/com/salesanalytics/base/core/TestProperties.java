package com.salesanalytics.base.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

public class TestProperties {

	private static final String SERVER_PROPERTY = "server";
	private static final String DEFAULT_SERVER = "local";
	private static final String COMMON_CONFIG_PATH = "config/common/common.properties";
	private static final String SERVER_CONFIG_PATH_FORMAT = "config/server/%s.properties";

	private static final Properties COMMON_PROPERTIES = loadProperties(COMMON_CONFIG_PATH);
	private static final Properties SERVER_PROPERTIES =
			loadProperties(String.format(SERVER_CONFIG_PATH_FORMAT, System.getProperty(SERVER_PROPERTY, DEFAULT_SERVER)));

	public String getServerProperty(final String name) {
		return getRequiredProperty(SERVER_PROPERTIES, name);
	}

	public String getCommonProperty(final String name) {
		return getRequiredProperty(COMMON_PROPERTIES, name);
	}

	public Optional<String> getOptionalCommonProperty(final String name) {
		return getProperty(COMMON_PROPERTIES, name);
	}

	private static String getRequiredProperty(final Properties properties, final String name) {
		return getProperty(properties, name).orElseThrow(() ->
				new IllegalStateException(String.format("Property [%s] is not defined", name)));
	}

	private static Optional<String> getProperty(final Properties properties, final String name) {
		final String systemValue = System.getProperty(name);
		final String value = isBlank(systemValue) ? properties.getProperty(name) : systemValue;
		return isBlank(value) ? Optional.empty() : Optional.of(value.trim());
	}

	private static boolean isBlank(final String value) {
		return value == null || value.isBlank();
	}

	private static Properties loadProperties(final String path) {
		final Properties properties = new Properties();
		try (final InputStream in = TestProperties.class.getClassLoader().getResourceAsStream(path)) {
			if (in == null) {
				throw new IllegalStateException(String.format("Config file [%s] not found on the test classpath", path));
			}
			properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new UncheckedIOException(String.format("Can not read config file [%s]", path), e);
		}
		return properties;
	}
}
