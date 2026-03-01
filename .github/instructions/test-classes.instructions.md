---
applyTo: "tests/src/test/**"
---

# Test Classes Conventions

## Class Hierarchy

- Extend `ApiBaseTest` as parent class — provides `httpClient` and `dbClient` via `@Autowired`
- Use an additional base class per endpoint (e.g. `GetAllGamesBaseTest`) for shared methods
- Each test class covers the entire endpoint functionality across multiple Jira tickets
- Annotate every test class with `@Log4j2`
- Do not repeat `@SpringBootTest` or `@ActiveProfiles("test")` — already inherited from `ApiBaseTest`

## Test Method Structure

- Follow **Given / When / Then** structure in every test method
- **Given** — data preparation: DB calls, expected result builders
- **When** — action: HTTP request via `httpClient`
- **Then** — assertions and verifications via AssertJ

## Reporting and Traceability

- Use `AllureSteps.logStep(log, description, runnable)` for void assertion steps
- Use `AllureSteps.logStepAndReturn(log, description, supplier)` for steps that return a value
- Use `@TmsLink("XSP-123")` or `@TmsLinks({@TmsLink("XSP-91"), @TmsLink("XSP-92")})` to link test cases from Jira

## Assertions

- Use only **AssertJ** — no Hamcrest, no JUnit assertions
- Always include `.as("Descriptive failure message")` on every assertion
- Use soft assertions or POJO comparison methods (e.g. `prepareExpectedAllGamesResponseList`) to build expected results

## Test Data

- Declare test data outside the test method — use `@MethodSource`, `@CsvSource`, etc.
- No hardcoded values in tests
- For POJO classes do not use primitive data types

## Naming

- **Test methods**: descriptive names explaining the scenario (e.g. `getAllVideoGamesPositiveTest`)
- Use `@DisplayName` for human-readable test descriptions — short summary only, no expected result
- **Packages**: lowercase, named after the endpoint (e.g. `getAllGames`)

## Configuration Beans

- `@Configuration` beans for test infrastructure live in the `config/` package
- `DbClientConfig` — wires `JdbcTemplate`, `ObjectMapper` (case-insensitive), and `DbClient`
- `HttpClientConfig` — wires `HttpClient` singleton and initializes it on `WebServerInitializedEvent`

