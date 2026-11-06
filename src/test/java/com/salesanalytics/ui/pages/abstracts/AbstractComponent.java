package com.salesanalytics.ui.pages.abstracts;

import com.microsoft.playwright.Page;
import org.slf4j.Logger;

public abstract class AbstractComponent {

	protected final Page page;
	protected final Logger logger;

	protected AbstractComponent(final Page page, final Logger logger) {
		this.page = page;
		this.logger = logger;
	}
}
