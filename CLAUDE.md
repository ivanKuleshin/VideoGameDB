# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Custom agents usage

You should use all available custom agents, before implementing work by yourself, check all available agents and if no
dedicated agents found, you can start work by yourself. Highly encourage to use custom agents for coding, Jira
interaction, reviews.

## Build Commands

```bash
# Build both modules + run Checkstyle (no tests)
mvn clean install -DskipTests

# Build app module only
mvn install -pl app -DskipTests

# Run app locally on port 8080
mvn spring-boot:run -pl app

# Run all component tests (auto-builds app module first)
mvn test -pl tests

# Skip the automatic app rebuild when app is already installed
mvn test -pl tests -Dexec.skip=true

# Run a single test class
mvn test -pl tests -Dtest=GetVideoGameByIdComponentTest

# Generate and serve Allure report
allure serve tests/target/allure-results
```

## AGENTS.md Reference

Read these files only when you need module-specific details — they are not loaded by default.

| File                                 | When to read                                                                                                                                                                                                                                       |
|--------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`AGENTS.md`](AGENTS.md)             | Project-level overview: module responsibilities, how the modules interact, top-level build commands, technology stack, coding standards, naming conventions, CI pipeline.                                                                          |
| [`app/AGENTS.md`](app/AGENTS.md)     | `app` module details: source layout, full endpoint reference, key design decisions (dual serialization, delete-even behavior, `releaseDate`/`released_on` mapping), DB schema, security config, how to add a new endpoint.                         |
| [`tests/AGENTS.md`](tests/AGENTS.md) | `tests` module details: source layout, mandatory test class pattern (`*BaseTest` / `*ComponentTest`), step pattern (`AllureSteps`), test data rules, all naming/assertion/model/builder/utility conventions, how to add tests for a new operation. |

## Architecture Overview

Two-module Maven project (`videogamedb-parent`):

- **`app/`** — Spring Boot 3.5.3 REST API. Controller → Service → Repository (JPA) over H2 in-memory DB. All business
  endpoints under `/app/videogames`. Supports both JSON and XML on every endpoint. HTTP Basic Auth (`test`/`test`).
- **`tests/`** — Black-box component test suite. Boots the full `App` context at a random port via
  `@SpringBootTest(RANDOM_PORT)`. Communicates only through HTTP (`HttpClient`/REST Assured) and JDBC (`H2DbClient`
  /JdbcTemplate). Never calls app internals directly.

The `tests` module declares `videogamedb-app` as a compile-scope dependency. An `exec-maven-plugin` execution at
`generate-resources` runs `mvn install` in `app/` first, so `mvn test -pl tests` is always self-contained.

Checkstyle (`codestyle/checkStyle.xml`) is enforced at `validate` phase across both modules (max line length 130, no
wildcard imports).

## App Module (`app/`)

**Endpoints** — all under `/app/videogames`, all produce/consume `application/json` and `application/xml`:

| Method | Path                            | Notes                                               |
|--------|---------------------------------|-----------------------------------------------------|
| GET    | `/videogames`                   | Returns `VideoGameList`                             |
| GET    | `/videogames/{videoGameId}`     | Throws if not found                                 |
| POST   | `/videogames`                   | Returns `{"status": "Record Added Successfully"}`   |
| PUT    | `/videogames/{videoGameId}`     | Returns updated entity                              |
| DELETE | `/videogames/{videoGameId}`     | Returns `{"status": "Record Deleted Successfully"}` |
| DELETE | `/videogames/delete-even-games` | Deletes up to 5 rows with even IDs via native query |

**DB schema**: `VIDEOGAME(ID, NAME, RELEASED_ON DATE, REVIEW_SCORE INT, CATEGORY, RATING)`. Seeded with IDs 1–10 on
every context start from `schema.sql`.

`releaseDate` maps to column `released_on` via `@Column(name = "released_on")`.

**Adding a new endpoint**: add handler to `VideoGameController` → implement in `VideoGameService` → add/extend
`VideoGameRepository` → add/update model XML annotations → add path to `Endpoint` enum in `tests`.

## Tests Module (`tests/`)

**Mandatory test class pattern** (enforced by Surefire which picks up only `**/*ComponentTest.class`):

```
<operationName>/
  <OperationName>BaseTest.java      ← extends ApiBaseTest; prepare*() helpers only, no @Test
  <OperationName>ComponentTest.java ← all @Test methods; extends *BaseTest
```

**Test data rules**:

- H2 seeded with IDs 1–10 as baseline on every context start.
- Tests inserting extra rows must use `VideoGameTestDataFixtures` entries (IDs 101–105) and clean up in `finally` via
  `dbClient.deleteVideoGameById(id)`.
- DB assertions use `CommonSteps.verifyGameExistsInDatabase()` / `verifyGameNotExistsInDatabase()`.

**XML responses** must be parsed via `XmlUtil.parse(response.asString(), Model.class)` — never deserialize XML through
REST Assured directly.

**`@TmsLink("XSP-NNN")`** on every `@Test` method; **`@DisplayName`** on every test class and method; **`@Log4j2`** on
every `*ComponentTest` class.

**Model conventions**:

- JSON: `model/api/json/` — `@Data`, `@JsonProperty` where field name differs.
- XML: `model/api/xml/` — `@Data`, `@JacksonXmlRootElement`, `@JacksonXmlProperty`, `@JacksonXmlElementWrapper`.
- DB: `model/db/` — `@Data`, `@JsonProperty` matching uppercase column names (e.g. `@JsonProperty("RELEASED_ON")`).
- Canonical comparison model is `VideoGameApiModel`; both JSON and XML responses map to it.

**Builders** live in `builder/` with fluent `with*()` methods and a terminal `build()` returning `Map<String, Object>`.
Provide sensible defaults so tests only override what they need.

**Adding tests for a new operation**: create `tests/src/test/java/com/ai/tester/<operationName>/` → add `*BaseTest` (
helpers) and `*ComponentTest` (@Test methods) → add to `Endpoint` enum if absent → add `VideoGameTestDataFixtures`
entries if new isolated IDs needed → add model classes under `model/api/json/` or `model/api/xml/`.

## Coding Standards

- No `throws` declarations — always `try-catch` with `throw new RuntimeException(...)`.
- Use Lombok to reduce boilerplate (`@Data`, `@Log4j2`, `@RequiredArgsConstructor`, etc.).
- No wildcard imports.
- Do not create unused code (avoid Boat Anchor anti-pattern).
- Do not add comments or Javadoc unless explicitly requested — code should be self-explanatory through naming.
- Do not create multiple MD files when summarizing changes unless explicitly asked.
- POJO fields must use object types (not primitives).
- Assertions: AssertJ only — no Hamcrest, no JUnit assertions. Always include `.as("Descriptive failure message")`.

## Naming Conventions

- **Classes**: PascalCase (e.g. `VideoGameService`, `ApiBaseTest`)
- **Methods**: camelCase with verb prefixes (`prepare*`, `create*`, `validate*`, `get*`, `check*`)
- **Variables**: camelCase, descriptive, no abbreviations
- **Constants**: `UPPER_SNAKE_CASE` for `static final` fields
- **Test packages**: lowercase, named after the endpoint (e.g. `getAllGames`, `getVideoGameById`)