---
name: component-testing
description: >-
  Guide for implementing component tests for Spring Boot applications.
  Use this when asked to create, write, review or implement automated test cases,
  component tests, or API tests. Should be used during code review.
---

# Component Tests Implementation Guide

## Project Context

- You should take the project context first, before implementing the tests. Read all three AGENTS.md files:
    - `AGENTS.md` — project overview, shared conventions, build commands
    - `app/AGENTS.md` — app module layout, endpoints, design decisions
    - `tests/AGENTS.md` — test infrastructure, mandatory patterns, test data rules

- Also read the path-specific custom instruction files:
    - `tests/.github/instructions/test-classes.instructions.md` — class hierarchy, assertions, test data, naming,
      models (`tests/src/test/**`)
    - `tests/.github/instructions/test-infrastructure.instructions.md` — clients, models, builders, endpoints,
      utilities (`tests/src/main/**`)

## Test Location

- Use test location - `tests/src/test/java/com/ai/tester` (unless explicitly specified otherwise)
- Organize by endpoint name, like `getAllGames` - `GetAllGamesComponentTest`

## Class Structure

1. Extend **ApiBaseTest** as parent class by default — provides `httpClient` and `dbClient` via `@Autowired`
2. Use Additional Base class like **GetAllGamesBaseTest** for each endpoint to reuse some common methods
3. Each test class covers the **entire endpoint functionality** regardless of how many Jira tickets describe it.
   When multiple tickets map to the same scenario (e.g., XSP-108 "returns 200" + XSP-109 "record persisted"),
   combine them into a **single test method** annotated with `@TmsLinks({@TmsLink("XSP-108"), @TmsLink("XSP-109")})`.
   Only split into separate methods when the tickets test genuinely different scenarios (e.g., happy path vs. 401
   error).
4. Annotate every test class with `@Log4j2` to have logger available
5. Spring Boot context is started with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@ActiveProfiles("test")` —
   already inherited from `ApiBaseTest`, do not repeat

## Main Rules

This skill adds test-specific rules:

1. Use soft assertions or POJO classes assertions with methods like `prepareExpectedAllGamesResponseList` to build
   expected result
2. Validate there are no hardcoded values in the test — game IDs, names, dates, scores must always come from DB
   queries or fixture constants, never as inline literals
3. Use `AllureSteps` class for reporting steps — see patterns below
4. Use `CommonSteps` class for reusable verification logic (database, response content checks)
5. You need to verify the content of the response even if it's missed in the Jira/Xray
6. Always fetch game from DB as test data — never construct expected values from inline literals
7. **Do not use `VideoGameBuilder`** — it is considered a bad approach for constructing test data. Always use
   **enum-based fixtures** (`VideoGameTestDataFixtures`) when you need to **insert** data into the DB. Fixtures
   keep IDs and field values in one place and are reusable across tests. Never pass inline object literals to
   `dbClient.insertVideoGame()` or build a request body from hardcoded strings.
8. Some main logical actions should be wrapped in `AllureSteps` methods. For example: creating a test data, verification
   of DB/response. We need to build a structured report with all main actions.
9. No hardcoded values in tests, especially in expected results. Use private constant dynamic DB objects or third-party
   libraries to generate values.
    - Bad example: `.contains("application/json"))`
    - Good example: `.contains(MediaType.APPLICATION_JSON_VALUE))`
10. Expected results from Jira/Xray are source of truth. Do not change expected result based on your assumptions or
    failed test
    runs. Main goal of testing is to verify that the implementation meets the requirements, not to make the test pass by
    changing expected.
11. Test cases with invalid Jira statuses like Canceled or Duplicate should not be implemented, it means they are not
    valid.

## Test Method Structure

- Follow **Given / When / Then** structure in every test method
- **Given** — data preparation: DB calls, expected result builders
- **When** — action: HTTP request via `httpClient`
- **Then** — assertions and verifications via AssertJ
- This structure should be followed in all test methods, even if it seems a bit redundant for simple
  cases. It helps to maintain consistency and readability across the entire test suite.

## Reporting and Traceability

- Allure step descriptions must describe **what is being verified**, not **which endpoint is called** — step text
  is a testing concern, not a routing concern. The step description should be readable without knowing the endpoint:
    - ❌ `"Send GET /videogames request"` — endpoint name hardcoded
    - ❌ `"Verify GET /videogames/{id} returns 200"` — endpoint name hardcoded
    - ✅ `"Send GET request to retrieve all video games"` — describes intent
    - ✅ `"Verify response status code is 200"` — describes the check
- Use `AllureSteps.logStep(log, description, runnable)` for void assertion steps
- Use `AllureSteps.logStepAndReturn(log, description, supplier)` for steps that return a value
- Use `@TmsLink("XSP-123")` or `@TmsLinks({@TmsLink("XSP-91"), @TmsLink("XSP-92")})` to link test cases from Jira to
  code

### Test Data Management: Fixture Approach and DDT

- Use **enum-based fixtures** to eliminate test data boilerplate, like `VideoGameTestDataFixtures`
- If possible, use DDT (Data-Driven Testing)
- Test data can be taken from manual test and put to fixture, DDT method, file or constant, but the condition should be
  checked explicitly in the test. For example:
    - VideoGame with id 99999 is not present in the DB and used in manual test case. In the code, in Given section, it
      should verified by query to DB. There is a chance that this data was added by another test and test will be flaky
      or fail consistently.

### Test-Specific Naming

- **Test Methods**: Descriptive names explaining scenario (e.g., `getAllVideoGamesPositiveTest`), but the test methods
  should not contain expected result, like `deleteAlreadyDeletedVideoGameReturns404Test`.
- Use `@DisplayName` for human-readable test descriptions, it should contain short summary of the test
  case, no expected result specified. In summary should be specified goal of the test, not expected result.

---

## Code Patterns

See [code-patterns.md](examples/code-patterns.md) for AllureSteps, @TmsLink, AssertJ, and Given/When/Then
examples.
