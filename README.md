# Sales Analytics tests

[![Tests](https://github.com/shcholokoff/MtrlSpcTestTask/actions/workflows/tests.yml/badge.svg)](https://github.com/shcholokoff/MtrlSpcTestTask/actions/workflows/tests.yml)

UI and API tests for the Sales Analytics application: Java 17, Maven, Cucumber 7, Playwright (UI), RestAssured (API).

# Tests run

### Local

##### Dependencies

| Tool    | Version | Install (macOS)           | Download                                                  |
| ------- | ------- | ------------------------- | --------------------------------------------------------- |
| Java    | 17+     | `brew install openjdk@17` | [adoptium.net](https://adoptium.net)                      |
| Maven   | 3.9+    | `brew install maven`      | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Node.js | 18+     | `brew install node`       | [nodejs.org](https://nodejs.org)                          |

Maven dependencies and Playwright browsers are downloaded automatically on the first test run.

##### Application under test

Start the application on http://localhost:3000 and keep it running:
```sh
cd sales-analytics-qa-task
npm start
```

The application is also included in `app/` (copy of the provided archive) for CI: `cd app && npm start`.

##### Tests

Execute all tests (scenarios run in parallel, 2 threads, see `junit-platform.properties`):
```sh
mvn clean test
```

Execute UI tests:
```sh
mvn clean test -Dcucumber.filter.tags="@ui"
```

Execute API tests:
```sh
mvn clean test -Dcucumber.filter.tags="@api"
```

IntelliJ IDEA: run `RunCucumberTest`.

(optional) Open Allure report:
```sh
mvn allure:serve
```

##### Configuration (vm options)

| Option               | Description                                                                                |
| -------------------- | ------------------------------------------------------------------------------------------ |
| `-Dserver`           | Environment file from `config/server`, default `local`                                     |
| `-Ddomain.url`       | API base URL, default `http://localhost:3000`                                              |
| `-Dui.domain.url`    | Application URL, default `http://localhost:3000`                                           |
| `-Dbrowser.name`     | `chromium` (default), `firefox` or `webkit`                                                |
| `-Dbrowser.channel`  | Installed browser instead of the bundled one: `chrome`, `msedge`                           |
| `-Dheadless`         | `false` shows the browser window, default `true`                                           |
| `-Dslow.mo.ms`       | Delay between Playwright actions in ms, default `0`                                        |
| `-Dtimeout.ms`       | Timeout for actions, assertions and API requests in ms, default `10000`                    |
| `-Dtrace.on.failure` | Save Playwright trace of failed UI scenarios to `target/playwright-traces`, default `true` |

Example: `mvn clean test -Dcucumber.filter.tags="@ui" -Dheadless=false -Dslow.mo.ms=500`

### CI

GitHub Actions ([tests.yml](.github/workflows/tests.yml)) on every push and pull request: code style check → start the application from `app/` → install Chromium → run all tests. Allure results, Playwright traces and logs are uploaded as the `test-results` artifact.

# Test design

Requirement numbers refer to `requirements.md` of the application.

### UI

`features/ui/transactions_filtering.feature` – *Selected sorting is kept when filters change, return no data or are cleared*

| Step                                                                           | Requirements     |
| ------------------------------------------------------------------------------ | ---------------- |
| Page load: default sorting, 10 rows sorted by date, table = API response       | 5, 7             |
| Apply 3 filters + sorting: #1010, #1001, `Showing 2 of 10`, table = API        | 3, 4, 8, 11      |
| Change one filter: sorting kept, #1004, #1010, #1001, #1007, `Showing 4 of 10` | 9                |
| Change filters to an empty combination: `No data available`, sorting kept      | 6, 9             |
| Clear: filters `Not selected`, sorting kept, 10 rows sorted by revenue         | 5, 10            |
| Each dropdown: exact list of options in order                                  | Available values |

- Scope: behavior implemented in the browser. Clear and keeping the sorting are client-side; the server receives only the resulting query.
- Two levels of assertions: rendered DOM and the intercepted `GET /api/transactions` request (query params) and response.
- Table = API response: every row is compared as text in the displayed column order, including revenue formatting.
- Assertions: Playwright web-first assertions (`hasText`, `hasCount`, `isVisible`) with auto-retry.
- Option label → API value: checked via request params for the options used in Apply and Clear; every API value is covered by the API scenario.
- Synchronization: wait for the `/api/transactions` response (a non-2xx status fails the step), then for the `Loading transactions…` message to disappear. DOM assertions retry on their own, so the wait does not affect their result.
- Locators: `getByRole` with accessible names; `getByText` only for elements without a role (counter, empty-state message).

### API

`features/api/transactions_api.feature` – *Every equivalence class of filters and sorting returns exactly the expected transactions*

| Parameter    | Equivalence classes                                                          |
| ------------ | ---------------------------------------------------------------------------- |
| `dateRange`  | omitted, `none`, `7`, `30`, `90`                                             |
| `region`     | omitted, `none`, `Europe`, `North America`, `Asia`                           |
| `category`   | omitted, `none`, `Furniture`, `Appliances`, `Lighting`                       |
| `sort`       | omitted (default `date_asc`), `date_asc`, `date_desc`, `rev_asc`, `rev_desc` |
| Combinations | every pair of filters, all three filters, filters with an empty result       |

- 15 requests: every class at least once; every pair of filters and all three together (3, 4); every sorting on filtered data (8); an empty result (6). Requirements 1, 2, 5 and 7 are covered by single-filter, `none` and omitted classes.
- The full cartesian product (256 requests, ~90 s) is replaced by one representative per class (~15 s).
- Date window boundary: `dateRange=7` is 2025-03-19…2025-03-25; #1010 (day 1) is included, #1009 (day 8) is excluded.
- Expected order IDs are explicit in the Gherkin table. Every returned transaction is compared field by field with the dataset row with the same ID.
- Contract: RestAssured `ResponseSpecification` (status, `Content-Type: application/json`, JSON Schema `schemas/transactions-response.json`); consistency of `meta.total`, `meta.returned` and `meta.filters` (defaults for omitted params).
- Out of scope: invalid values (`dateRange=abc`, `region=europe`, unknown `sort`); their behaviour is not defined in `requirements.md`.
