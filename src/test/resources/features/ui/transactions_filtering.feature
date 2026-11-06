@ui
Feature: Sales Transactions table - filtering and sorting
  As a sales analyst
  I want my sorting to stay the same while I change, empty or clear the filters
  So that I can compare filtered and full data in the same order

  Scenario: Selected sorting is kept when filters change, return no data or are cleared
    Given the user opens the Sales Analytics page
    Then the sorting is "Date: Oldest → Newest"
    And the table shows 10 transactions sorted by "Date: Oldest → Newest"
    And the table shows exactly the transactions returned by the API
    When the user applies the following selection:
      | Date range | Last 90 days        |
      | Region     | Europe              |
      | Category   | Furniture           |
      | Sort by    | Revenue: High → Low |
    Then the table shows order IDs:
      | #1010 |
      | #1001 |
    And the transaction counter shows "Showing 2 of 10 transactions"
    And the table shows exactly the transactions returned by the API
    When the user applies the following selection:
      | Category | Not selected |
    Then the sorting is "Revenue: High → Low"
    And the table shows order IDs:
      | #1004 |
      | #1010 |
      | #1001 |
      | #1007 |
    And the transaction counter shows "Showing 4 of 10 transactions"
    When the user applies the following selection:
      | Region   | Asia       |
      | Category | Appliances |
    Then the "No data available" message is displayed
    And the transaction counter shows "Showing 0 of 10 transactions"
    And the sorting is "Revenue: High → Low"
    And the table data was requested with:
      | dateRange | 90         |
      | region    | Asia       |
      | category  | Appliances |
      | sort      | rev_desc   |
    When the user clears the filters
    Then all filters are reset to "Not selected"
    And the sorting is "Revenue: High → Low"
    And the table shows 10 transactions sorted by "Revenue: High → Low"
    And the transaction counter shows "Showing 10 of 10 transactions"
    And the table shows exactly the transactions returned by the API
    And the table data was requested with:
      | dateRange | none     |
      | region    | none     |
      | category  | none     |
      | sort      | rev_desc |
    And the dropdowns contain exactly the following options:
      | Date range | Not selected, Last 7 days, Last 30 days, Last 90 days                                 |
      | Region     | Not selected, Europe, North America, Asia                                             |
      | Category   | Not selected, Furniture, Appliances, Lighting                                         |
      | Sort by    | Date: Oldest → Newest, Date: Newest → Oldest, Revenue: Low → High, Revenue: High → Low |
