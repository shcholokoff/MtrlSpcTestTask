package com.salesanalytics.base.core.parameters;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constants {

	public static final String NOT_SELECTED_FILTER_VALUE = "none";

	public static final Path TRACES_DIRECTORY = Paths.get("target", "playwright-traces");

	private Constants() {
	}
}
