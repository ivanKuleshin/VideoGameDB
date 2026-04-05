---
name: component-testing
description: >-
  Guide for implementing component tests for Spring Boot applications using JUnit 5, REST Assured,
  Allure, and AssertJ. Trigger this skill whenever the user wants to add, write, fix, or review a
  test — including casual phrases like "cover this endpoint", "add a test for X", "make sure this is
  tested", "the test is failing", or "what's the right way to write this test". Also trigger when the
  user asks about @TmsLink, AllureSteps, VideoGameTestDataFixtures, CommonSteps, or ApiBaseTest.
---

# Component Tests Implementation Guide

## Test Location

- Use test location - `tests/src/test/java/com/ai/tester` (unless explicitly specified otherwise)
- Organize by endpoint name, like `getAllGames` - `GetAllGamesComponentTest`

## Class Structure

1. Extend **ApiBaseTest** as parent class by default — provides `dbClient` and `commonSteps` via `@Autowired`
2. Use Additional Base class like **GetAllGamesBaseTest** for each endpoint to reuse some common methods;
   it also holds the `@Autowired *ApiActions` field for that endpoint
3. Each test class covers the **entire endpoint functionality** regardless of how many Jira tickets describe it.
   When multiple tickets map to the same scenario (e.g., XSP-108 "returns 200" + XSP-109 "record persisted"),
   combine them into a **single test method** annotated with `@TmsLinks({@TmsLink("XSP-108"), @TmsLink("XSP-109")})`.
   Only split into separate methods when the tickets test genuinely different scenarios (e.g., happy path vs. 401
   error).
4. Annotate every test class with `@Log4j2` to have logger available
5. Spring Boot context is started with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@ActiveProfiles("test")` —
   already inherited from `ApiBaseTest`, do not repeat

## Class Member Ordering

All classes must follow Google Checkstyle member ordering (`ModifierOrder` rule in `codestyle/checkStyle.xml`):

1. Static nested classes
2. Instance fields
3. Constructor(s)
4. Public static methods
5. Public instance methods
6. Private static methods
7. Private instance methods

See [code-patterns.md](references/code-patterns.md) for a full annotated example.

## Main Rules

This skill adds test-specific rules:

1. Use soft assertions or POJO classes assertions with methods like `prepareExpectedAllGamesResponseList` to build
   expected result
2. Validate there are no hardcoded values in the test, like game IDs, names, dates, http statuses, etc. Scores must
   always come from DB queries or fixture constants, never as inline literals
3. Use `AllureSteps` class for reporting steps — see patterns below
4. Use `CommonSteps` class for reusable verification logic (database, response content checks). This class's methods
   already contain Allure steps, but check each method individually to ensure the presence of Allure steps.
5. You need to verify the content of the response even if it's missed in the Jira/Xray
6. Allure step descriptions must describe **what is being verified**, not **which endpoint is called** — step text
   is a testing concern, not a routing concern. The step description should be readable without knowing the endpoint:
    - ❌ `"Send GET /videogames request"` — endpoint name hardcoded
    - ❌ `"Verify GET /videogames/{id} returns 200"` — endpoint name hardcoded
    - ✅ `"Send GET request to retrieve all video games"` — describes intent
    - ✅ `"Verify response status code is 200"` — describes the check
7. Always fetch game from DB as test data — never construct expected values from inline literals
8. Use **enum-based fixtures** (`VideoGameTestDataFixtures`) when you need to **insert** data into the DB. Fixtures
   keep IDs and field values in one place and are reusable across tests. Never pass inline object literals to
   `dbClient.insertVideoGame()` or build a request body from hardcoded strings.
9. Some main logical actions should be wrapped in `AllureSteps` methods. For example: creating a test data, verification
   of DB/response. We need to build a structured report with all main actions.

## Test Method Structure

- Follow **Given / When / Then** structure in every test method. Mark these sections with comment
- **Given** — data preparation: DB calls, expected result builders
- **When** — action: HTTP request via `*ApiActions` (never `httpClient` directly)
- **Then** — assertions and verifications via AssertJ
- This structure should be followed in all test methods, even if it seems a bit redundant for simple
  cases. It helps to maintain consistency and readability across the entire test suite.

## Reporting and Traceability

- Use `AllureSteps.logStep(log, description, runnable)` for void assertion steps
- Use `AllureSteps.logStepAndReturn(log, description, supplier)` for steps that return a value
- Use `@TmsLink("XSP-123")` or `@TmsLinks({@TmsLink("XSP-91"), @TmsLink("XSP-92")})` to link test cases from Jira to
  code

## Quick Example

Minimal end-to-end test skeleton — the reference point for all patterns in this skill:

```java

@Log4j2
class GetVideoGameByIdComponentTest extends GetVideoGameByIdBaseTest {

    @Test
    @TmsLink("XSP-42")
    @DisplayName("Get video game by ID returns correct game data")
    void getVideoGameByIdPositiveTest() {
        // Given
        VideoGameDbModel expectedGame = AllureSteps.logStepAndReturn(log,
            "Fetch expected game from database",
            () -> dbClient.getVideoGameById(1).orElseThrow());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game by ID",
            () -> apiActions.getById(expectedGame.getId(), ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode()).as("Status code").isEqualTo(HttpStatus.OK.value()));
        AllureSteps.logStep(log, "Verify response body matches expected game",
            () -> validateResponse(response, expectedGame));
    }
}
```

### Test Data Management: Fixture Approach and DDT

- Use **enum-based fixtures** to eliminate test data boilerplate, like `VideoGameTestDataFixtures`
- Use **DDT (Data-Driven Testing)** when the same test logic applies to multiple inputs (e.g., testing
  the same endpoint with several fixture variants). Prefer separate test methods when scenarios have
  genuinely different expected behaviour or require different assertions.

  ```
  @ParameterizedTest
  @MethodSource("gameFixtures")
  @TmsLink("XSP-55")
  @DisplayName("Create video game persists each fixture variant")
  void createVideoGameDdtTest(VideoGameTestDataFixtures fixture) {
      VideoGameDbModel game = fixture.getGameData();
      try {
          // When
          Response response = AllureSteps.logStepAndReturn(log, "Send POST request to create video game",
              () -> apiActions.post(fixture.toRequestBody(), ContentType.JSON));

          // Then
          AllureSteps.logStep(log, "Verify game is persisted in database",
              () -> commonSteps.verifyGameExistsInDatabase(game.getId()));
      } finally {
          dbClient.deleteVideoGameById(game.getId());
      }
  }

  static Stream<VideoGameTestDataFixtures> gameFixtures() {
      return Stream.of(
          VideoGameTestDataFixtures.SHOOTER_GAME,
          VideoGameTestDataFixtures.PUZZLE_GAME
      );
  }
  ```

## Common Mistakes

1. **Repeating `@SpringBootTest` or `@ActiveProfiles`** — both are already inherited from `ApiBaseTest`;
   adding them again creates a second context and slows the suite.
2. **Inline literals for expected values** — IDs, names, scores, and dates must come from `dbClient` queries
   or `VideoGameTestDataFixtures` constants or constant/enums for expected results. Hardcoded literals make tests fragile and break the no-hardcoded-values
   rule.
3. **Assertions outside `AllureSteps.logStep`** — bare `assertThat(...)` calls in the `// Then` block are
   invisible in the Allure report. Every assertion must be wrapped so it appears as a named step.
4. **Calling app internals directly** — all HTTP interactions must go through `*ApiActions`; all DB interactions
   through `dbClient`. Tests must never import `HttpClient`, `Endpoint` paths, or `AuthType` — those belong
   to the Actions layer. Importing or calling `VideoGameResource` or `App` classes from a test breaks black-box isolation.
5. **Missing `try-finally` cleanup after fixture insert** — rows inserted via `dbClient.insertVideoGame()` persist
   for the life of the Spring context. Always delete in a `finally` block to avoid polluting later tests.

## Test-Specific Naming

- **Test Methods**: Descriptive names explaining scenario (e.g., `getAllVideoGamesPositiveTest`)
- Use `@DisplayName` for human-readable test descriptions, it should contain short summary of the test
  case, no expected result specified

---

## Code Patterns

See [code-patterns.md](references/code-patterns.md) for ready-to-copy snippets:

- `AllureSteps.logStep` and `logStepAndReturn` usage
- `@TmsLink` / `@TmsLinks` annotations
- AssertJ assertions with `.as()` failure messages
- Full Given/When/Then skeleton
- Fixture-based request body construction (`VideoGameTestDataFixtures.toRequestBody()`)

## TAF Layered Architecture

See [taf-pattern.md](references/taf-pattern.md) for the full layered architecture reference:

- Layer diagram and responsibility boundaries (Test → Actions → Driver → Infrastructure)
- Which layer owns endpoint paths, `AuthType`, and URL building
- How `*ApiActions` classes are structured and wired into `*BaseTest`
- Cross-endpoint injection pattern (e.g., `DeleteVideoGameBaseTest` using both `DeleteVideoGameApiActions`
  and `GetAllGamesApiActions`)
- Step-by-step guide for adding a new endpoint to the framework

## SOLID Principles

See [solid-patterns.md](references/solid-patterns.md) for how SOLID is applied across every layer of the TAF:

- **S** — why `HttpClient`, `*ApiActions`, `*BaseTest`, and `*ComponentTest` each have exactly one responsibility
- **O** — how to extend with new auth variants, `AuthType` values, or endpoints without modifying existing classes
- **L** — what every `*Actions` implementation must honour to be safely substitutable
- **I** — why each endpoint has its own narrow `*Actions` interface, and how to inject two interfaces when a test spans two endpoints
- **D** — the mandatory rule: `*BaseTest` always autowires the `*Actions` **interface**, never the concrete `*ApiActions` class

