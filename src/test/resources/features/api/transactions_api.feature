@api
Feature: Transactions API - filtering and sorting
  GET /api/transactions must apply all provided filters together (AND logic)
  and sort only the transactions that remain after filtering.
  The application's current date is 2025-03-25. "Last N days" means the N calendar days ending on it, both inclusive.
  Each request is a representative of one equivalence class of a parameter or of a combination of classes.
  "omitted" means the parameter is not sent, "no data" means an empty result.

  Scenario: Every equivalence class of filters and sorting returns exactly the expected transactions
    Given the application contains the following transactions:
      | id    | date       | region        | category   | revenue |
      | #1001 | 2025-01-05 | Europe        | Furniture  | 1200    |
      | #1002 | 2025-01-12 | North America | Appliances | 850     |
      | #1003 | 2025-01-18 | Asia          | Lighting   | 640     |
      | #1004 | 2025-02-03 | Europe        | Appliances | 2300    |
      | #1005 | 2025-02-14 | North America | Furniture  | 1550    |
      | #1006 | 2025-02-20 | Asia          | Furniture  | 980     |
      | #1007 | 2025-03-01 | Europe        | Lighting   | 760     |
      | #1008 | 2025-03-11 | North America | Appliances | 1430    |
      | #1009 | 2025-03-18 | Asia          | Lighting   | 2100    |
      | #1010 | 2025-03-25 | Europe        | Furniture  | 1680    |
    When the client requests transactions with:
      | dateRange | region        | category   | sort      | expected order IDs                                                   |
      | omitted   | omitted       | omitted    | omitted   | #1001, #1002, #1003, #1004, #1005, #1006, #1007, #1008, #1009, #1010 |
      | none      | none          | none       | date_desc | #1010, #1009, #1008, #1007, #1006, #1005, #1004, #1003, #1002, #1001 |
      | 7         | omitted       | omitted    | omitted   | #1010                                                                |
      | 30        | omitted       | omitted    | omitted   | #1007, #1008, #1009, #1010                                           |
      | 90        | none          | none       | rev_asc   | #1003, #1007, #1002, #1006, #1001, #1008, #1005, #1010, #1009, #1004 |
      | omitted   | Europe        | omitted    | date_desc | #1010, #1007, #1004, #1001                                           |
      | omitted   | North America | omitted    | date_asc  | #1002, #1005, #1008                                                  |
      | omitted   | Asia          | omitted    | omitted   | #1003, #1006, #1009                                                  |
      | omitted   | omitted       | Furniture  | omitted   | #1001, #1005, #1006, #1010                                           |
      | omitted   | omitted       | Appliances | rev_asc   | #1002, #1008, #1004                                                  |
      | omitted   | omitted       | Lighting   | omitted   | #1003, #1007, #1009                                                  |
      | 30        | omitted       | Lighting   | omitted   | #1007, #1009                                                         |
      | 30        | Europe        | none       | rev_desc  | #1010, #1007                                                         |
      | 90        | Europe        | Furniture  | rev_desc  | #1010, #1001                                                         |
      | omitted   | Asia          | Appliances | omitted   | no data                                                              |
    Then every response has status 200 and matches the transactions schema
    And every response contains exactly the expected transactions with all fields in the expected order
