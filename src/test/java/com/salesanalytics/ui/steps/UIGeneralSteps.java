package com.salesanalytics.ui.steps;

import static com.salesanalytics.base.core.parameters.PropertyNames.UI_DOMAIN_URL;

import com.salesanalytics.base.core.PlaywrightManager;
import com.salesanalytics.base.core.TestProperties;
import com.salesanalytics.ui.pages.analytics.SalesAnalyticsPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIGeneralSteps {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final PlaywrightManager playwrightManager;
	protected final TestProperties properties;

	public UIGeneralSteps(final PlaywrightManager playwrightManager, final TestProperties properties) {
		this.playwrightManager = playwrightManager;
		this.properties = properties;
	}

	public SalesAnalyticsPage openSalesAnalyticsPage() {
		logger.info("Open Sales Analytics page");
		return new SalesAnalyticsPage(playwrightManager.getPage(), logger, properties.getServerProperty(UI_DOMAIN_URL)).openPage();
	}
}
