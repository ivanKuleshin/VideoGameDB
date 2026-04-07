# AGENTS.md

## Project Overview

Two-module Maven project: `app` (Spring Boot REST API) and `tests` (component test suite). Each module has its own
`AGENTS.md` with module-specific details — see [`app/AGENTS.md`](app/AGENTS.md) and [
`tests/AGENTS.md`](tests/AGENTS.md).

## Module Responsibilities

| Module   | Role                                                                                                                       |
|----------|----------------------------------------------------------------------------------------------------------------------------|
| `app/`   | Spring Boot + Spring MVC REST API over H2 (Spring Data JPA). Business endpoints remain under `/app/**`. Entry: `App.java`. |
| `tests/` | Black-box component tests. Drives `app` over HTTP; never calls app internals directly.                                     |

## How the Modules Interact

`tests` declares `videogamedb-app` as a dependency (default Maven compile scope) and boots the full `App` context via
`@SpringBootTest(RANDOM_PORT)`. An `exec-maven-plugin` execution at the `generate-resources` phase auto-runs
`mvn install` in `app/` before tests compile, so `mvn test -pl tests` is always self-contained.

## Top-Level Build Commands

```bash
# Build both modules and run Checkstyle (no tests)
mvn clean install -DskipTests

# Build app module only
mvn install -pl app -DskipTests

# Run all component tests (builds app automatically)
mvn test -pl tests

# Skip the automatic app rebuild when already installed
mvn test -pl tests -Dexec.skip=true
```

## Technology Stack

- **Java 21**, **Spring Boot 3.5.3**, **Maven 3.9+**
- `app`: Spring MVC, Spring Data JPA, H2, springdoc OpenAPI, Spring Security (HTTP Basic)
- `tests`: JUnit 5, Spring Boot Test (`RANDOM_PORT`), REST Assured, AssertJ, Allure, Log4j2, Jackson (JSON + XML), H2,
  Spring JDBC (`JdbcTemplate`)

## Coding Standards

- Follow clean code and SOLID principles; prioritize readability and maintainability.
- Use Lombok to reduce boilerplate (`@Log4j2`, `@Data`, `@RequiredArgsConstructor`, etc.).
- No `throws` declarations — `try-catch` with `throw new RuntimeException(...)`.
- No wildcard imports; imports organized by package hierarchy.
- Do not create code that is not used — avoid Boat Anchor anti-pattern.
- Code should be self-explanatory; add comments/Javadoc only when explicitly requested.
- Checkstyle enforced via `codestyle/checkStyle.xml`.
- In tests, mark a known-failing `@Test` with `@KnownIssue("XSP-NNN")` (backed by `KnownIssueCondition`) to disable it
  instead of deleting or commenting it out.

## Naming Conventions

- **Classes**: PascalCase with descriptive names (e.g., `VideoGameService`, `ApiBaseTest`).
- **Methods**: camelCase with verb prefixes (`prepare*`, `create*`, `validate*`, `get*`, `check*`).
- **Variables**: camelCase, descriptive names avoiding abbreviations.
- **Constants**: UPPER_SNAKE_CASE for `static final` fields.

## Comments and Javadoc

- Do not add custom comments and Javadoc if not asked
- Code should be self-explanatory through clear naming and structure

## Final MD files

- Do not create multiple MD files when you need to summarize changes, your steps, etc., unless user asks you to do so.

## CI Pipeline

- Pull-request CI flow is documented in `CI_PIPELINE.md` (`build -> test -> publish-report`).
- Build job runs `mvn clean install -DskipTests`; test job runs component tests; report job publishes Allure to GitHub
  Pages.

