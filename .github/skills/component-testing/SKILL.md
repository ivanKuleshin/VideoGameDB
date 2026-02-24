---
name: component-testing
description: Complete guide for implementing component tests for Spring Boot applications.
---

# Component Tests Implementation Guide

## Test Location

- Use test location - `tests/src/test/java/com/ai/tester` (unless explicitly specified otherwise)
- Organize by endpoint name, like `getAllGames` - `GetAllGamesComponentTest`

## Class Structure

1. Extend **BaseApiTest** as parent class by default
2. Use Additional Base class like **GetAllGamesBaseTest** for each endpoint to reuse some common methods
3. Each test class should cover the entire endpoint functionality, despite one endpoint is covered with more than 1 Jira
   ticket

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
9. Follow the given/when/then structure in test methods like in `GetAllGamesComponentTest`
10. Use `AllureSteps` class for reporting steps, use example from `getAllVideoGamesPositiveTest` test
11. Use `@TmsLink` or `@TmsLinks` to link test cases from Jira to code

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

