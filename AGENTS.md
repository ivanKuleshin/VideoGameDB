# AGENTS.md

## Architecture Overview

Two-module Maven project: `app` (Spring Boot REST API) and `tests` (component test suite).

- **`app/`** — Spring Boot + Jersey (JAX-RS), Spring JDBC, H2 in-memory DB, Spring Security (HTTP Basic Auth). All
  endpoints prefixed `/app`, support JSON and XML. Entry: `App.java`.
- **`tests/`** — Black-box component tests. Start the full `App` context at a random port and drive it via HTTP. Tests
  never call app internals directly — only `HttpClient` (HTTP) and `H2DbClient` (JDBC) are used.

## Critical Source Layout

```
tests/src/main/java/   ← shared infrastructure (compiled as main sources, not test)
tests/src/test/java/   ← test classes + Spring @Configuration beans only
```

> Infrastructure lives in `src/main/java` intentionally — this allows it to be reused across test modules without
> test-scoped dependency tricks.

## Test Class Structure

Every API operation has its own package under `tests/src/test/java/com/ai/tester/`:

```
getAllGames/
  GetAllGamesBaseTest.java       ← extends ApiBaseTest, holds prepare* helpers
  GetAllGamesComponentTest.java  ← @Test methods only, extends *BaseTest
```

Surefire picks up **only** `**/*ComponentTest.class` — all test methods must be in `*ComponentTest` classes.

`ApiBaseTest` wires `HttpClient`, `DbClient`, and `CommonSteps` via `@Autowired`. New operation packages must extend
it (directly or via an intermediate `*BaseTest`).

## Key Infrastructure Files

| File                                                                   | Role                                                                           |
|------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `tests/src/test/java/.../ApiBaseTest.java`                             | Root test base; `@SpringBootTest(RANDOM_PORT)` + `@ActiveProfiles("test")`     |
| `tests/src/test/java/.../config/HttpClientConfig.java`                 | Initialises `HttpClient` singleton on `WebServerInitializedEvent`              |
| `tests/src/test/java/.../config/DbClientConfig.java`                   | Creates `JdbcTemplate` + `H2DbClient` beans                                    |
| `tests/src/main/java/.../client/http/HttpClient.java`                  | Bill-Pugh singleton; `init()` must be called before use                        |
| `tests/src/main/java/.../client/db/H2DbClient.java`                    | JDBC-based DB client for test setup/verification                               |
| `tests/src/main/java/.../allure/AllureSteps.java`                      | `logStep()` / `logStepAndReturn()` — every test step must use these            |
| `tests/src/main/java/.../data/Endpoint.java`                           | Enum of all API paths (`VIDEOGAMES`, `VIDEOGAME_BY_ID`, `DELETE_EVEN_GAMES`)   |
| `tests/src/main/java/.../data/fixtures/VideoGameTestDataFixtures.java` | Pre-defined test games (IDs 101–105) as enum entries with `.getGameData()`     |
| `tests/src/main/java/.../builder/VideoGameBuilder.java`                | Fluent builder returning `Map<String, Object>` for REST Assured request bodies |

## Step Pattern (mandatory)

Every named action in a test must be wrapped with `AllureSteps`:

```
// void step
AllureSteps.logStep(log, "Description",() ->

assertThat(...));

// step returning a value
Response response = AllureSteps.logStepAndReturn(log, "Description", () -> httpClient.get(...));
```

## Test Data Management

- DB is H2 in-memory, seeded from `schema.sql` on each context start — baseline data is always present.
- Use `VideoGameTestDataFixtures` enum entries (IDs 101–105) for insert/delete scenarios; always clean up in `finally`
  blocks.
- Use `VideoGameBuilder` for custom payloads (defaults: id=100, name="Test Game").
- `CommonSteps.verifyGameExistsInDatabase()` / `verifyGameNotExistsInDatabase()` for shared DB assertions.

## Build & Test Commands

```bash
# Run all component tests (auto-builds app module first)
mvn test -pl tests

# Skip app rebuild when already installed locally
mvn test -pl tests -Dexec.skip=true

# Run a single test class
mvn test -pl tests -Dtest=GetAllGamesComponentTest

# Generate and open Allure report
allure serve tests/target/allure-results
```

## Adding a New API Operation

1. Create package `tests/src/test/java/com/ai/tester/<operationName>/`.
2. Add `<OperationName>BaseTest extends ApiBaseTest` with `prepare*` helpers.
3. Add `<OperationName>ComponentTest extends <OperationName>BaseTest` with `@Test` methods.
4. Add new endpoint to `Endpoint` enum if needed.
5. Add new `VideoGameTestDataFixtures` entries if new IDs are required.

## Conventions

- No `throws` declarations — always `try-catch` with `throw new RuntimeException(...)`.
- Lombok `@Log4j2` on every test class; use `log` field in `AllureSteps` calls.
- `@TmsLink("XSP-NNN")` on every `@Test` method linking to Jira/Xray.
- XML responses are parsed via `XmlUtil.parse(response.asString(), ModelClass.class)`.
- `application-test.properties` values can be overridden by env vars (e.g. `HTTP_CLIENT_BASE_URL`).

