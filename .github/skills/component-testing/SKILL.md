---
name: component-testing
description: Complete guide for implementing component tests for Spring Boot applications.
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

1. Use JUnit 5
2. Use only AssertJ for assertions
3. Use soft assertions or POJO classes assertions with methods like `prepareExpectedAllGamesResponseList` to build
   expected result
4. Test Data should be declared outside the TC, use JUnit5 like @MethodSource, @CsvSource, etc
5. Use Lombok for POJO classes and in entire TAF
6. For POJO classes do not use primitive data types
7. Validate there are no hardcoded values in the test
8. Maintainable and reusable code is a must
9. Follow the given/when/then structure in test methods like in `GetAllGamesComponentTest`. Where Given is to data
   preparation(DB calls, expected result builders, etc.), When is to action (HTTP request) and Then is to assertions and
   verifications. This structure should be followed in all test methods, even if it seems a bit redundant for simple
   cases. It helps to maintain consistency and readability across the entire test suite.
10. Use `AllureSteps` class for reporting steps — see patterns below
11. Use `@TmsLink` or `@TmsLinks` to link test cases from Jira to code
12. You need to verify the content of the response even if it's missed in the Jira/Xray 

### Formatting and Structure

- **Package Naming**: Lowercase, domain-based organization (e.g., `client`, `getAllGames`)
- **Class Organization**: Fields first, constructors, public methods, protected methods, private methods
- **Line Length**: Keep lines concise and readable, break long method chains
- **Indentation**: Consistent 4-space indentation (enforced by CheckStyle)
- **Import Organization**: No wildcard imports, organized by package hierarchy

### Naming Conventions

- **Classes**: PascalCase with descriptive names (e.g., `BaseApiTest`, `HttpClient`, `GetAllGamesTest`)
- **Methods**: camelCase with verb prefixes indicating action
    - `prepare*()` - for test data preparation methods
    - `create*()` - for object creation/factory methods
    - `validate*()` - for validation methods
    - `get*()` - for retrieval methods
    - `check*()` - for boolean condition checks
- **Variables**: camelCase, descriptive names avoiding abbreviations except domain-specific
- **Constants**: UPPER_SNAKE_CASE for static final fields
- **Test Methods**: Descriptive names explaining scenario (e.g., `getAllVideoGamesPositiveTest`)
- **Test Naming**: Use `@DisplayName` for human-readable test descriptions, it should contain short summary of the test
  case, no expected result specified

---

## Code Patterns

### AllureSteps — void step

```java
AllureSteps.logStep(log, "Verify response status code is 200",
    () -> assertThat(response.getStatusCode())
        .as("Response status code should be 200")
        .isEqualTo(200));
```

### AllureSteps — step with return value

```java
Response response = AllureSteps.logStepAndReturn(log,
    "Send GET request to get all video games",
    () -> httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON));
```

### @TmsLink — single ticket

```java

@Test
@TmsLink("XSP-91")
@DisplayName("...")
void myTest() { ...}
```

### @TmsLinks — multiple tickets

```java

@Test
@TmsLinks({
    @TmsLink("XSP-91"),
    @TmsLink("XSP-92")
})
@DisplayName("...")
void myTest() { ...}
```

### AssertJ — always include `.as()` message

```java
assertThat(actual)
    .as("Descriptive failure message")
    .isEqualTo(expected);
```

### Given/When/Then structure

```java
void myTest() {
    // Given
    SomeModel data = AllureSteps.logStepAndReturn(log, "Prepare test data", () -> {
        // setup and return
    });

    // When
    Response response = AllureSteps.logStepAndReturn(log, "Send HTTP request", () ->
        httpClient.get(ENDPOINT.getPath(), ContentType.JSON));

    // Then
    AllureSteps.logStep(log, "Verify ...", () ->
        assertThat(response.getStatusCode()).as("...").isEqualTo(200));
}
```

