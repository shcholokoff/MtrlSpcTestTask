package com.salesanalytics.base.core.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHooks {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Before(order = 0)
	public void logScenarioStart(final Scenario scenario) {
		logger.info("Start scenario [{}] with tags {}", scenario.getName(), scenario.getSourceTagNames());
	}

	@After(order = 0)
	public void logScenarioFinish(final Scenario scenario) {
		logger.info("Finish scenario [{}] with status [{}]", scenario.getName(), scenario.getStatus());
	}
}
