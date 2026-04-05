# tests/AGENTS.md

## Module Overview

Black-box component test suite for the `app` module. Boots the full `App` Spring context at a random port and drives
it over HTTP. Never calls app internals — interaction is through `HttpClient` (REST Assured) and `H2DbClient` (JDBC).

## Technology Stack

- **JUnit 5** — test engine (`@Test`, `@DisplayName`, `@TestInstance(PER_CLASS)`)
- **Spring Boot Test** — `@SpringBootTest` with `RANDOM_PORT`, `@ActiveProfiles("test")`
- **REST Assured** — HTTP client (`HttpClient` singleton, wraps `RequestSpecification`)
- **AssertJ** — assertions
- **Allure** — reporting (`AllureSteps` utility class, `allure-junit5` integration)
- **Log4j2** — logging (`@Log4j2` from Lombok)
- **Jackson** — JSON deserialization (`ObjectMapper`, `JsonMapper`) and XML (`XmlMapper`, `jackson-dataformat-xml`)
- **H2** — in-memory database for component tests
- **Spring JDBC** — `JdbcTemplate` used in `H2DbClient`

## Source Layout

```
tests/src/main/java/       ← shared infrastructure (compiled as main sources, reusable across modules)
  client/http/HttpClient   ← Bill-Pugh singleton; wraps REST Assured RequestSpecification
  client/db/
    DbClient               ← interface
    H2DbClient             ← JdbcTemplate impl (SELECT/INSERT/DELETE)
  model/api/json/          ← Jackson response models (GetAllGamesResponseModel, VideoGameApiModel, …)
  model/api/xml/           ← JAXB/XmlMapper response models (VideoGameXmlModel, …)
  model/db/VideoGameDbModel← DB row model (@JsonProperty for case-insensitive column mapping)
  data/Endpoint            ← Enum of all API paths (VIDEOGAMES, VIDEOGAME_BY_ID, DELETE_EVEN_GAMES)
  data/fixtures/VideoGameTestDataFixtures ← Pre-built test games IDs 101–105 with .getGameData()
  steps/CommonSteps        ← @Component; shared DB verify steps (verifyGameExists/NotExists)
  allure/AllureSteps       ← Utility; logStep() / logStepAndReturn() wrapping Allure + Log4j2
  util/XmlUtil             ← Parses XML strings via XmlMapper
  util/DateUtil            ← Converts between epoch-millis and "yyyy-MM-dd" strings

tests/src/test/java/       ← test classes and their Spring @Configuration beans only
  ApiBaseTest              ← @SpringBootTest + @ActiveProfiles("test"); injects httpClient, dbClient, commonSteps
  config/
    HttpClientConfig       ← @Configuration; initialises HttpClient singleton on WebServerInitializedEvent
    DbClientConfig         ← @Configuration; creates JdbcTemplate + H2DbClient beans
  getAllGames/…
  getVideoGameById/…
  deleteVideoGame/…
  deleteEvenGames/…
```

## Test Class Structure (mandatory pattern)

```
<operationName>/
  <OperationName>BaseTest.java       ← extends ApiBaseTest; prepare*() helpers only
  <OperationName>ComponentTest.java  ← @Test methods only; extends *BaseTest
```

Surefire includes **only** `**/*ComponentTest.class`. All `@Test` methods must live in `*ComponentTest` classes.

## Step Pattern (mandatory)

Every named action in a test body must be wrapped:

```
// void step
AllureSteps.logStep(log, "Step description", () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value()));

// step with return value
Response response = AllureSteps.logStepAndReturn(log, "Send GET /videogames",
    () -> httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON));
```

## Test Data Rules

- H2 is seeded from `schema.sql` on every context start (IDs 1–10 always present as baseline).
- Tests that insert extra rows must use `VideoGameTestDataFixtures` entries (IDs 101–105) and clean up
  in a `finally` block via `dbClient.deleteVideoGameById(id)`.
- `CommonSteps.verifyGameExistsInDatabase()` / `verifyGameNotExistsInDatabase()` for shared DB assertions.

## Adding a New API Operation

1. Create `tests/src/test/java/com/ai/tester/<operationName>/`.
2. Add `<OperationName>BaseTest extends ApiBaseTest` — `prepare*` helpers only, no `@Test`.
3. Add `<OperationName>ComponentTest extends <OperationName>BaseTest` — `@Test` + `@TmsLink` + `@DisplayName` on every
   method.
4. Add the endpoint to `Endpoint` enum if not already present.
5. Add new `VideoGameTestDataFixtures` entries if new isolated IDs are needed.
6. Add response model classes under `model/api/json/` or `model/api/xml/` as required.

## Build & Test Commands

```bash
# Run all component tests (auto-builds app module first via exec-maven-plugin)
mvn test -pl tests

# Skip the automatic app rebuild when app is already installed locally
mvn test -pl tests -Dexec.skip=true

# Run a single test class
mvn test -pl tests -Dtest=GetAllGamesComponentTest

# Generate and open Allure report
allure serve tests/target/allure-results
```

## Configuration

`application-test.properties` activates under `@ActiveProfiles("test")`. All HTTP client values can be overridden by
environment variables (e.g. `HTTP_CLIENT_BASE_URL`, `HTTP_CLIENT_USERNAME`, `HTTP_CLIENT_PASSWORD`).

## Conventions Specific to This Module

- `@Log4j2` on every `*ComponentTest` class; the `log` field is passed to every `AllureSteps` call.
- `@TmsLink("XSP-NNN")` on every `@Test` method to link to the Jira/Xray test case.
- XML response bodies are never deserialized by REST Assured directly — use
  `XmlUtil.parse(response.asString(), Model.class)`.
- `VideoGameDbModel` uses `@JsonProperty("RELEASED_ON")` / `@JsonProperty("REVIEW_SCORE")` because
  `H2DbClient` maps column-name keys case-insensitively via Jackson.

## Test Classes Conventions

### Class Hierarchy

- Extend `ApiBaseTest` as parent class — provides `httpClient` and `dbClient` via `@Autowired`
- Use an additional base class per endpoint (e.g. `GetAllGamesBaseTest`) for shared methods
- Each test class covers the entire endpoint functionality across multiple Jira tickets
- Annotate every test class with `@Log4j2`
- Do not repeat `@SpringBootTest` or `@ActiveProfiles("test")` — already inherited from `ApiBaseTest`

### Assertions

- Use only **AssertJ** — no Hamcrest, no JUnit assertions
- Always include `.as("Descriptive failure message")` on every assertion
- Use soft assertions or POJO comparison methods (e.g. `prepareExpectedAllGamesResponseList`) to build expected results

### Test Data

- Declare test data outside the test method — use `@MethodSource`, `@CsvSource`, etc.
- No hardcoded values in tests — always fetch from DB or use fixtures
- For POJO classes do not use primitive data types

### Naming

- **Test methods**: descriptive names explaining the scenario (e.g. `getAllVideoGamesPositiveTest`)
- Use `@DisplayName` for human-readable test descriptions — short summary only, no expected result
- **Packages**: lowercase, named after the endpoint (e.g. `getAllGames`)
- Test classes are picked up by Surefire via `**/*ComponentTest.class` pattern
- Each API endpoint has its own package (e.g. `getAllGames/`) containing a `*BaseTest` and a `*ComponentTest`

### Configuration Beans

- `@Configuration` beans for test infrastructure live in the `config/` package
- `DbClientConfig` — wires `JdbcTemplate`, `ObjectMapper` (case-insensitive), and `DbClient`
- `HttpClientConfig` — wires `HttpClient` singleton and initializes it on `WebServerInitializedEvent`
- `CommonSteps` — reusable verification logic (database, response content checks, etc.)

## Test Infrastructure Conventions

### Client Conventions

- `HttpClient` is a singleton (`HttpClient.getInstance()`) initialized via `HttpClientConfig`
- HTTP methods: `get(path, contentType)`, `post(path, body, contentType)`, `put(path, body, contentType)`,
  `delete(path, contentType)`
- `DbClient` interface is implemented by `H2DbClient` using `JdbcTemplate`
- DB queries return `VideoGameDbModel`; `getReleaseDateAsString()` converts epoch millis to date string

### Model Conventions

- JSON response models: `model/api/json/` — use `@Data`, `@JsonProperty` where field name differs
- XML response models: `model/api/xml/` — use `@Data`, `@JacksonXmlRootElement`, `@JacksonXmlProperty`,
  `@JacksonXmlElementWrapper`
- DB models: `model/db/` — use `@Data`, `@JsonProperty` matching DB column names (uppercase)
- Shared canonical model for comparisons is `VideoGameApiModel` — both JSON and XML responses are mapped to it

### Builder Conventions

- Test data builders live in `builder/` package
- Use fluent `with*()` methods and a terminal `build()` returning `Map<String, Object>`
- Builders must provide sensible defaults for all fields so tests only override what they need

### Endpoint Conventions

- API endpoints are defined as an enum in `data/Endpoint` with a `@Getter path` field
- Always reference endpoints via the enum constant (e.g. `VIDEOGAMES.getPath()`)

### Utility Conventions

- Utility classes are `final` with a private constructor (or `@NoArgsConstructor(access = PRIVATE)`)
- `XmlUtil.parse(String, Class<T>)` — parses XML strings using a shared `XmlMapper`
- `DateUtil.epochMillisToDateString(long)` — converts epoch millis to `LocalDate.toString()`

### Properties Conventions

- Test properties in `application-test.properties` support env-var overrides (e.g. `${BASE_URL:http://localhost}`)
