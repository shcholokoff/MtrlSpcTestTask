package com.salesanalytics.ui.pages.abstracts;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;

public abstract class AbstractPage extends AbstractComponent {

	private final String pageUrl;

	protected AbstractPage(final Page page, final Logger logger, final String pageUrl) {
		super(page, logger);
		this.pageUrl = pageUrl;
	}

	protected void navigate() {
		logger.info("Open page [{}]", pageUrl);
		page.navigate(pageUrl);
	}
}
