# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Communication style
- Be concise and to the point. Use less words to convey the same meaning.
- Use bullet points and tables to organize information clearly.
- Avoid unnecessary explanations or justifications. Focus on what needs to be done.

## Persistent Memory

At the start of every conversation, read [.claude/memory/MEMORY.md](.claude/memory/MEMORY.md) and apply all entries found there.

## Custom agents usage

Always check available agents before implementing work yourself. Available agents:

| Agent                      | When to use                                                             |
|----------------------------|-------------------------------------------------------------------------|
| `test-automation-engineer` | Writing, fixing, or refactoring test code                               |
| `test-code-reviewer`       | Reviewing test code quality against skill rules                         |
| `jira-xray-researcher`     | Fetching Jira/Xray ticket details before planning or implementing tests |

If no dedicated agent fits, implement directly.

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

## Architecture Overview

Two-module Maven project (`videogamedb-parent`):

- **`app/`** — Spring Boot REST API. Controller → Service → Repository (JPA) over H2. All endpoints under
  `/app/videogames`. JSON + XML. HTTP Basic Auth (`test`/`test`).
- **`tests/`** — Black-box component tests. Drives `app` over HTTP and JDBC only — never calls app internals. Boots the
  full `App` context at a random port via `@SpringBootTest(RANDOM_PORT)`.

Read the appropriate AGENTS.md before working on a module — they are the source of truth for conventions, patterns, and
step-by-step guides:

| File                                 | When to read                                                                                                              |
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| [`AGENTS.md`](AGENTS.md)             | Technology stack, coding standards, naming conventions, CI pipeline                                                       |
| [`app/AGENTS.md`](app/AGENTS.md)     | Any `app/` work: endpoints, DB schema, security config, key design decisions, how to add an endpoint                      |
| [`tests/AGENTS.md`](tests/AGENTS.md) | Any `tests/` work: test class pattern, step pattern, test data rules, model/builder/utility conventions, how to add tests |