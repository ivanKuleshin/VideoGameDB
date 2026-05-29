# tests/AGENTS.md

## Module Overview

Black-box component test suite for the `app` module. Boots the full `App` Spring context at a random port and drives
it over HTTP. Never calls app internals — interaction is through `HttpClient` (REST Assured) and `H2DbClient` (JDBC).

## Technology Stack

- **JUnit 5** — test engine (`@Test`, `@DisplayName`, `@TestInstance(PER_CLASS)`)
- **Spring Boot Test** — `@SpringBootTest` with `RANDOM_PORT`, `@ActiveProfiles("test")`
- **REST Assured** — HTTP client (`HttpClient` Spring bean, wraps `RequestSpecification`)
- **AssertJ** — assertions
- **Allure** — reporting (`AllureSteps` utility class, `allure-junit5` integration)
- **Log4j2** — logging (`@Log4j2` from Lombok)
- **Jackson** — JSON deserialization (`ObjectMapper`, `JsonMapper`) and XML (`XmlMapper`, `jackson-dataformat-xml`)
- **H2** — in-memory database for component tests
- **Spring JDBC** — `JdbcTemplate` used in `H2DbClient`

## Source Layout

```
tests/src/main/java/       ← shared infrastructure (compiled as main sources, reusable across modules)
  actions/api/             ← per-operation HTTP action beans (@Component); concrete *ApiActions, no interfaces
    get/getAll/GetAllGamesApiActions, post/PostVideoGameApiActions, put/UpdateVideoGameApiActions,
    delete/DeleteVideoGameApiActions, delete/DeleteEvenGamesApiActions
  client/http/HttpClient   ← plain Spring bean built by HttpClientConfig; wraps REST Assured RequestSpecification
  client/db/
    DbClient               ← interface
    H2DbClient             ← JdbcTemplate impl (SELECT/INSERT/DELETE)
  model/api/json/          ← Jackson response models (GetAllGamesResponseModel, VideoGameApiModel, …)
  model/api/xml/           ← XmlMapper wrapper/request/response models (GetAllGamesXmlResponseModel, …)
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
    HttpClientConfig       ← @Configuration; builds the HttpClient bean and calls init() on WebServerInitializedEvent
    DbClientConfig         ← @Configuration; creates JdbcTemplate + H2DbClient beans
    TestSupportConfig      ← @Configuration; @ComponentScan of actions + steps (test-context-only scan boundary)
  getAllGames/…
  getVideoGameById/…
  deleteVideoGame/…
  deleteEvenGames/…
```

## Test Class Structure (mandatory pattern)

```
<operationName>/
  <OperationName>BaseTest.java       ← extends ApiBaseTest; injects the operation's *ApiActions bean, exposes fixtures
                                       via protected getter methods (e.g. getJsonFixture()), prepare*() helpers only
  <OperationName>ComponentTest.java  ← @Test methods only; extends *BaseTest; calls getters to resolve fixtures locally
```

Surefire includes **only** `**/*ComponentTest.class`. All `@Test` methods must live in `*ComponentTest` classes.

## Step Pattern (mandatory)

Every named action in a test body must be wrapped:

```
// void step
AllureSteps.logStep(log, "Step description", () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value()));

// step with return value
Response response = AllureSteps.logStepAndReturn(log, "Send GET /videogames",
    () -> apiActions.getAllGames(ContentType.JSON));
```

## Test Data Rules

- H2 is seeded from `schema.sql` on every context start (IDs 1–10 always present as baseline).
- Tests that insert extra rows must use `VideoGameTestDataFixtures` entries (IDs 101–105) and clean up
  in a `finally` block via `dbClient.deleteVideoGameById(id)`.
- `CommonSteps.verifyGameExistsInDatabase()` for shared DB assertions; `verifyGameNotExistsInDatabase()` returns
  `Optional<VideoGameDbModel>` (empty on success).

## Adding a New API Operation

1. Create `tests/src/test/java/com/ai/tester/<operationName>/`.
2. Add a concrete `<OperationName>ApiActions` `@Component` under `actions/api/...` that wraps `HttpClient` for the
   operation (no interface — inject the concrete type).
3. Add `<OperationName>BaseTest extends ApiBaseTest` — `@Autowired` the `*ApiActions` bean, expose fixtures via
   protected getter methods returning `VideoGameTestDataFixtures`, add `prepare*` helpers, no `@Test`.
4. Add `<OperationName>ComponentTest extends <OperationName>BaseTest` — `@Test` + `@TmsLink` + `@DisplayName` on every
   method.
5. Add the endpoint to `Endpoint` enum if not already present.
6. Add new `VideoGameTestDataFixtures` entries if new isolated IDs are needed.
7. Add response model classes under `model/api/json/` or `model/api/xml/` as required.

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
- `HttpClientConfig` — builds the `HttpClient` bean and initializes it on `WebServerInitializedEvent`
- `TestSupportConfig` — `@ComponentScan` of `actions` + `steps`; keeps test beans out of the production `App` scan
- `CommonSteps` — reusable verification logic; `verifyGameExistsInDatabase(log, id)` (existence),
  `verifyGameNameMatches(...)`, and a combined `verifyGameExistsInDatabase(log, id, name)` overload;
  `verifyGameNotExistsInDatabase(log, id)` returns `Optional<VideoGameDbModel>` (empty on success)

## Test Infrastructure Conventions

### Client Conventions

- `HttpClient` is a plain Spring bean created and `init()`-ed by `HttpClientConfig` on the `WebServerInitializedEvent`
  (no static `getInstance()` singleton); inject it where needed
- HTTP methods take an `AuthType` (`DEFAULT`/`NONE`/`WRONG`): `get(path, contentType, authType)`,
  `post(path, body, contentType, authType)`, `put(path, body, contentType, authType)`,
  `delete(path, contentType, authType)` — usually called via the `*ApiActions` beans, not directly
- `DbClient` interface is implemented by `H2DbClient` using `JdbcTemplate`
- DB queries return `VideoGameDbModel`; `getReleaseDateAsString()` converts epoch millis to date string

### Model Conventions

- Canonical model: `VideoGameApiModel` (`model/api/json/`) is the single model for both JSON and XML — it carries
  `@JsonProperty` and `@JacksonXmlRootElement(localName = "videoGame")`, and `XmlUtil.parse(...)` deserializes XML into
  it. There is no separate XML game model.
- XML wrapper/request models: `model/api/xml/` — use `@Data`, `@JacksonXmlRootElement`, `@JacksonXmlProperty`,
  `@JacksonXmlElementWrapper`; list children are typed as `VideoGameApiModel`
- DB models: `model/db/` — use `@Data`, `@JsonProperty` matching DB column names (uppercase)

### Endpoint Conventions

- API endpoints are defined as an enum in `data/Endpoint` with a `@Getter path` field
- Always reference endpoints via the enum constant (e.g. `VIDEOGAMES.getPath()`)

### Utility Conventions

- Utility classes are `final` with a private constructor (or `@NoArgsConstructor(access = PRIVATE)`)
- `XmlUtil.parse(String, Class<T>)` — parses XML strings using a shared `XmlMapper`
- `XmlUtil.serialize(Object)` — serializes an object to an XML string using the same `XmlMapper`
- `DateUtil.epochMillisToDateString(long)` — converts epoch millis to `LocalDate.toString()`

### Properties Conventions

- Test properties in `application-test.properties` support env-var overrides (e.g. `${HTTP_CLIENT_BASE_URL:http://localhost}`)
