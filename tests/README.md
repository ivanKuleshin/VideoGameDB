# VideoGameDB – Tests Module

Component test suite for the **VideoGameDB** Spring Boot application.  
Tests start the real application context against an in-memory H2 database and drive it over HTTP with REST Assured.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Running Tests](#running-tests)
- [Allure Report](#allure-report)
- [Checkstyle](#checkstyle)

---

## Tech Stack

| Tool             | Role                                |
|------------------|-------------------------------------|
| JUnit 5          | Test engine                         |
| Spring Boot Test | Application context (`RANDOM_PORT`) |
| REST Assured 5   | HTTP client                         |
| AssertJ          | Fluent assertions                   |
| H2               | In-memory database                  |
| Spring JDBC      | `JdbcTemplate`-based DB client      |
| Allure 2         | Test reporting                      |
| Log4j2           | Logging                             |
| Jackson          | JSON / XML deserialization          |
| Lombok           | Boilerplate reduction               |

---

## Project Structure

```
tests/
├── src/
│   ├── main/java/          # Shared infrastructure (clients, models, builders, utils)
│   └── test/java/          # Test classes and their Spring @Configuration beans
│       └── com/ai/tester/
│           ├── ApiBaseTest.java          # Base class wiring HttpClient & DbClient
│           └── getAllGames/
│               ├── GetAllGamesBaseTest.java       # Operation-scoped base & helpers
│               └── GetAllGamesComponentTest.java  # Concrete test cases
└── pom.xml
```

Each API operation lives in its own package containing a `*BaseTest` (shared setup/helpers) and a `*ComponentTest` (
concrete `@Test` methods).  
Surefire picks up test classes matching `**/*ComponentTest.class`.

---

## Prerequisites

- Java 21+
- Maven 3.9+
- The `app` module does **not** need to be built separately — the `tests` build triggers `mvn install` in the `app`
  module automatically during the `generate-resources` phase.

---

## Running Tests

### Run all component tests

```bash
mvn test -pl tests
```

### Run from the project root

```bash
mvn test --projects tests
```

### Run a single test class

```bash
mvn test -pl tests -Dtest=GetAllGamesComponentTest
```

### Skip the automatic app build (when `app` is already installed locally)

```bash
mvn test -pl tests -Dexec.skip=true
```

> Tests run against the `test` Spring profile (`@ActiveProfiles("test")`), which activates the H2 in-memory database.

---

## Allure Report

### Generate and open the report after a test run

```bash
mvn allure:report -pl tests          # generates report in target/site/allure-maven-plugin/
mvn allure:serve -pl tests           # generates + opens in the browser
```

### Using the bundled Allure CLI

A standalone Allure CLI is bundled under `.allure/allure-2.32.0/`.

```bash
# Generate a static report from raw results
.allure/allure-2.32.0/bin/allure generate tests/target/allure-results -o tests/target/allure-report --clean

# Open the generated report in the browser
.allure/allure-2.32.0/bin/allure open tests/target/allure-report
```

Raw Allure result files are written to `tests/target/allure-results/` after each test run.

---

## Checkstyle

Checkstyle is enforced on all `*.java`, `*.properties`, and `*.xml` files using the shared ruleset at
`codestyle/checkStyle.xml` (Google style base).

### Verify code style

```bash
mvn checkstyle:check -pl tests
```

### Generate a Checkstyle report (without failing the build)

```bash
mvn checkstyle:checkstyle -pl tests
```

The report is written to `tests/target/checkstyle-result.xml`.  
Violations with severity `error` will fail the build during `checkstyle:check`.
