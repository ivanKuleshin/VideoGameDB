---
name: component-testing
description: >-
  Guide for implementing component tests for Spring Boot applications.
  Use this when asked to create, write, or implement automated test cases,
  component tests, or API tests.
---

# Component Tests Implementation Guide

## Test Location

- Use test location - `tests/src/test/java/com/ai/tester` (unless explicitly specified otherwise)
- Organize by endpoint name, like `getAllGames` - `GetAllGamesComponentTest`

## Class Structure

1. Extend **ApiBaseTest** as parent class by default — provides `httpClient` and `dbClient` via `@Autowired`
2. Use Additional Base class like **GetAllGamesBaseTest** for each endpoint to reuse some common methods
3. Each test class should cover the entire endpoint functionality, despite one endpoint is covered with more than 1 Jira
   ticket
4. Annotate every test class with `@Log4j2` to have logger available
5. Spring Boot context is started with `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `@ActiveProfiles("test")` —
   already inherited from `ApiBaseTest`, do not repeat

## Main Rules

This skill adds test-specific rules:

1. Use soft assertions or POJO classes assertions with methods like `prepareExpectedAllGamesResponseList` to build
   expected result
2. Validate there are no hardcoded values in the test
3. Use `AllureSteps` class for reporting steps — see patterns below
4. Use `CommonSteps` class for reusable verification logic (database, response content checks)
5. You need to verify the content of the response even if it's missed in the Jira/Xray
6. Make sure in Allure step no mentions of endpoint names at all, it's hardcoded data
7. Always fetch game from DB as a test data
8. Use fixtures for cases when you need to insert data

## Test Method Structure

- Follow **Given / When / Then** structure in every test method
- **Given** — data preparation: DB calls, expected result builders
- **When** — action: HTTP request via `httpClient`
- **Then** — assertions and verifications via AssertJ
- This structure should be followed in all test methods, even if it seems a bit redundant for simple
  cases. It helps to maintain consistency and readability across the entire test suite.

## Reporting and Traceability

- Use `AllureSteps.logStep(log, description, runnable)` for void assertion steps
- Use `AllureSteps.logStepAndReturn(log, description, supplier)` for steps that return a value
- Use `@TmsLink("XSP-123")` or `@TmsLinks({@TmsLink("XSP-91"), @TmsLink("XSP-92")})` to link test cases from Jira to
  code

### Test Data Management: Fixture Approach and DDT

- Use **enum-based fixtures** to eliminate test data boilerplate, like `VideoGameTestDataFixtures`
- If possible, use DDT (Data-Driven Testing)

### Test-Specific Naming

- **Test Methods**: Descriptive names explaining scenario (e.g., `getAllVideoGamesPositiveTest`)
- Use `@DisplayName` for human-readable test descriptions, it should contain short summary of the test
  case, no expected result specified

---

## Code Patterns

See [code-patterns.md](examples/code-patterns.md) for AllureSteps, @TmsLink, AssertJ, and Given/When/Then
examples.
