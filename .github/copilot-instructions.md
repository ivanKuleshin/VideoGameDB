# Project Overview

Test Automation Framework (TAF) for component testing of Spring Boot application

# Module Structure

- `tests/src/main/java` — shared test infrastructure (clients, models, builders, utils, steps)
- `tests/src/test/java` — test classes and their configuration (`@Configuration` beans)
- Test classes are picked up by Surefire via `**/*ComponentTest.class` pattern
- Each API operation has its own package (e.g. `getAllGames/`) containing a `*BaseTest` and a `*ComponentTest`

# Technology Stack

- **JUnit 5** — test engine (`@Test`, `@DisplayName`, `@TestInstance(PER_CLASS)`)
- **Spring Boot Test** — `@SpringBootTest` with `RANDOM_PORT`, `@ActiveProfiles("test")`
- **REST Assured** — HTTP client (`HttpClient` singleton, wraps `RequestSpecification`)
- **AssertJ** — assertions
- **Allure** — reporting (`AllureSteps` utility class, `allure-junit5` integration)
- **Log4j2** — logging (`@Log4j2` from Lombok)
- **Jackson** — JSON deserialization (`ObjectMapper`, `JsonMapper`) and XML (`XmlMapper`, `jackson-dataformat-xml`)
- **H2** — in-memory database for component tests
- **Spring JDBC** — `JdbcTemplate` used in `H2DbClient`

# Coding Principles

- Follow clean code and SOLID principles
- Prioritize readability and maintainability
- Use Lombok as much as possible to reduce boilerplate code
- Never use `throws`, always handle exceptions with try-catch blocks and throw `RuntimeException` in catch block

# Naming Conventions

- **Classes**: PascalCase with descriptive names (e.g., `ApiBaseTest`, `HttpClient`)
- **Methods**: camelCase with verb prefixes (`prepare*`, `create*`, `validate*`, `get*`, `check*`)
- **Variables**: camelCase, descriptive names avoiding abbreviations
- **Constants**: UPPER_SNAKE_CASE for static final fields
- **Imports**: no wildcard imports, organized by package hierarchy

# Comments and Javadoc

- Do not add custom comments and Javadoc if not asked
- Code should be self-explanatory through clear naming and structure

# Final MD files

- Do not create multiple MD files when you need to summarize changes, your steps, etc., unless I ask you to do so.

