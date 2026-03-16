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

## Assertions

- Use only **AssertJ** — no Hamcrest, no JUnit assertions
- Always include `.as("Descriptive failure message")` on every assertion
- Use soft assertions or POJO comparison methods (e.g. `prepareExpectedAllGamesResponseList`) to build expected results

## Test Data

- Declare test data outside the test method — use `@MethodSource`, `@CsvSource`, etc.
- No hardcoded values in tests — always fetch from DB or use fixtures
- For POJO classes do not use primitive data types
- ⚠️ **Do not use `VideoGameBuilder`** as the primary source of test data — it is considered a bad approach.
  Always use `VideoGameTestDataFixtures` enum entries instead.

## Naming

- **Test methods**: descriptive names explaining the scenario (e.g. `getAllVideoGamesPositiveTest`)
- Use `@DisplayName` for human-readable test descriptions — short summary only, no expected result
- **Packages**: lowercase, named after the endpoint (e.g. `getAllGames`)
- Test classes are picked up by Surefire via `**/*ComponentTest.class` pattern
- Each API endpoint has its own package (e.g. `getAllGames/`) containing a `*BaseTest` and a `*ComponentTest`

## Configuration Beans

- `@Configuration` beans for test infrastructure live in the `config/` package
- `DbClientConfig` — wires `JdbcTemplate`, `ObjectMapper` (case-insensitive), and `DbClient`
- `HttpClientConfig` — wires `HttpClient` singleton and initializes it on `WebServerInitializedEvent`
- `CommonSteps` — reusable verification logic (database, response content checks, etc.)

## Models management

1. All requests and responses should be wrapped into POJO classes and put to the `model.api` folder.
2. All DB tables should be we wrapped into POJO classes and put to the `model.db` folder

