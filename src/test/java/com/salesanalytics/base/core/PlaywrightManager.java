package com.salesanalytics.base.core;

import static com.salesanalytics.base.core.parameters.Constants.TRACES_DIRECTORY;
import static com.salesanalytics.base.core.parameters.PropertyNames.BROWSER_CHANNEL;
import static com.salesanalytics.base.core.parameters.PropertyNames.BROWSER_NAME;
import static com.salesanalytics.base.core.parameters.PropertyNames.HEADLESS;
import static com.salesanalytics.base.core.parameters.PropertyNames.SLOW_MO_MS;
import static com.salesanalytics.base.core.parameters.PropertyNames.TIMEOUT_MS;
import static com.salesanalytics.base.core.parameters.PropertyNames.TRACE_ON_FAILURE;

import java.nio.file.Path;
import java.util.Optional;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

public class PlaywrightManager {

	private final TestProperties properties;

	private Playwright playwright;
	private BrowserContext browserContext;
	private Page page;

	public PlaywrightManager(final TestProperties properties) {
		this.properties = properties;
	}

	public Page getPage() {
		if (page == null) {
			page = getBrowserContext().newPage();
		}
		return page;
	}

	public boolean isPageOpened() {
		return page != null;
	}

	public Optional<Path> close(final boolean saveTrace, final String traceName) {
		Path tracePath = null;
		try {
			if (browserContext != null && isTraceOnFailure()) {
				if (saveTrace) {
					tracePath = TRACES_DIRECTORY.resolve(traceName.replaceAll("[^A-Za-z0-9._-]+", "_") + ".zip");
					browserContext.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
				} else {
					browserContext.tracing().stop();
				}
			}
		} finally {
			if (playwright != null) {
				playwright.close();
			}
			playwright = null;
			browserContext = null;
			page = null;
		}
		return Optional.ofNullable(tracePath);
	}

	private Playwright getPlaywright() {
		if (playwright == null) {
			playwright = Playwright.create();
		}
		return playwright;
	}

	private BrowserContext getBrowserContext() {
		if (browserContext == null) {
			final BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
					.setHeadless(Boolean.parseBoolean(properties.getCommonProperty(HEADLESS)))
					.setSlowMo(Double.parseDouble(properties.getCommonProperty(SLOW_MO_MS)));
			properties.getOptionalCommonProperty(BROWSER_CHANNEL).ifPresent(channel -> launchOptions.setChannel(channel));

			final Browser browser = getBrowserType().launch(launchOptions);
			browserContext = browser.newContext(new Browser.NewContextOptions().setViewportSize(1440, 900));
			browserContext.setDefaultTimeout(getTimeout());
			if (isTraceOnFailure()) {
				browserContext.tracing().start(new Tracing.StartOptions().setScreenshots(true)
																		 .setSnapshots(true));
			}
		}
		return browserContext;
	}

	private BrowserType getBrowserType() {
		final String browserName = properties.getCommonProperty(BROWSER_NAME);
		return switch (browserName.toLowerCase()) {
			case "chromium" -> getPlaywright().chromium();
			case "firefox" -> getPlaywright().firefox();
			case "webkit" -> getPlaywright().webkit();
			default -> throw new IllegalArgumentException(
					String.format("Unsupported browser [%s], use chromium, firefox or webkit", browserName));
		};
	}

	private double getTimeout() {
		return Double.parseDouble(properties.getCommonProperty(TIMEOUT_MS));
	}

	private boolean isTraceOnFailure() {
		return Boolean.parseBoolean(properties.getCommonProperty(TRACE_ON_FAILURE));
	}
}
