package com.salesanalytics.ui.pages.fragments;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import com.salesanalytics.ui.pages.abstracts.AbstractComponent;
import com.salesanalytics.ui.pages.metadata.FilterDropdownType;
import org.slf4j.Logger;

public class FiltersFragment extends AbstractComponent {

	private static final String APPLY_BUTTON_NAME = "Apply";
	private static final String CLEAR_BUTTON_NAME = "Clear";

	private final Locator applyButton;
	private final Locator clearButton;

	public FiltersFragment(final Page page, final Logger logger) {
		super(page, logger);
		this.applyButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(APPLY_BUTTON_NAME).setExact(true));
		this.clearButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CLEAR_BUTTON_NAME).setExact(true));
	}

	public FiltersFragment selectOptionInDropdown(final FilterDropdownType dropdown, final String option) {
		logger.info("Select [{}] option in \"{}\" dropdown", option, dropdown.getLabel());
		getDropdown(dropdown).selectOption(new SelectOption().setLabel(option));
		return this;
	}

	public Locator getOptions(final FilterDropdownType dropdown) {
		return getDropdown(dropdown).getByRole(AriaRole.OPTION);
	}

	public Locator getSelectedOption(final FilterDropdownType dropdown) {
		return getDropdown(dropdown).getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setSelected(true));
	}

	public void clickOnApplyButton() {
		logger.info("Click on \"{}\" button", APPLY_BUTTON_NAME);
		applyButton.click();
	}

	public void clickOnClearButton() {
		logger.info("Click on \"{}\" button", CLEAR_BUTTON_NAME);
		clearButton.click();
	}

	private Locator getDropdown(final FilterDropdownType dropdown) {
		return page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(dropdown.getLabel()).setExact(true));
	}
}
