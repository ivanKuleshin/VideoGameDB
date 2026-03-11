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

## Shared Conventions (both modules)

- Java 21, Spring Boot 3.5.3, Maven 3.9+.
- `app` uses Spring MVC controllers (`controller/`), service layer (`service/`), and Spring Data JPA repositories (
  `repository/`).
- No wildcard imports; imports organized by package hierarchy.
- No `throws` declarations — `try-catch` with `throw new RuntimeException(...)`.
- Lombok used to reduce boilerplate (`@Log4j2`, `@Data`, `@RequiredArgsConstructor`, etc.).
- Checkstyle enforced via `codestyle/checkStyle.xml`.

## CI Pipeline

- Pull-request CI flow is documented in `CI_PIPELINE.md` (`build -> test -> publish-report`).
- Build job runs `mvn clean install -DskipTests`; test job runs component tests; report job publishes Allure to GitHub
  Pages.

