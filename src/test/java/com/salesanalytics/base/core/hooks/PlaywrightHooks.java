package com.salesanalytics.base.core.hooks;

import static com.salesanalytics.base.core.parameters.PropertyNames.TIMEOUT_MS;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.salesanalytics.base.core.PlaywrightManager;
import com.salesanalytics.base.core.TestProperties;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaywrightHooks {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final PlaywrightManager playwrightManager;

	public PlaywrightHooks(final PlaywrightManager playwrightManager) {
		this.playwrightManager = playwrightManager;
	}

	@BeforeAll
	public static void configurePlaywrightAssertions() {
		PlaywrightAssertions.setDefaultAssertionTimeout(Double.parseDouble(new TestProperties().getCommonProperty(TIMEOUT_MS)));
	}

	@After("@ui")
	public void closePlaywright(final Scenario scenario) {
		final boolean isFailed = scenario.isFailed();
		if (isFailed && playwrightManager.isPageOpened()) {
			final byte[] screenshot = playwrightManager.getPage().screenshot(new Page.ScreenshotOptions().setFullPage(true));
			scenario.attach(screenshot, "image/png", "Screenshot on failure");
		}
		playwrightManager.close(isFailed, scenario.getName()).ifPresent(tracePath -> {
			logger.info("Playwright trace saved to [{}]", tracePath.toAbsolutePath());
			scenario.log("Playwright trace saved to " + tracePath.toAbsolutePath());
		});
	}
}
