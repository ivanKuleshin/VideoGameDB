# Skill Review: `db-testing`

## Overview

A practical guide for database testing with H2, JdbcTemplate, and enum-based fixtures. At **256 lines** it is the
longest custom skill in the repo and approaching the territory where progressive disclosure starts to matter. Has a
`references/` directory with at least two files (`code-patterns.md`, `db-client-patterns.md`).

---

## Pros

- **Comprehensive fixture pattern** — the `VideoGameTestDataFixtures` enum pattern is well-explained with
  `getGameData()` and `getGameDataWithId()` variants, showing both the "why" and the "how".
- **Concrete assertion examples** — positive (record present), negative (record absent), and "unrelated data
  untouched" patterns are all illustrated with real code.
- **Two reference files** — extended patterns and SQL/DbClient specifics are properly delegated, keeping the body
  from ballooning further.
- **Cleanup strategy section** — explicitly covers automatic, between-class, and within-class cleanup, which is a
  frequent source of test pollution bugs.
- **Best practices section** — six numbered items with a clear rationale (e.g., *"Never write SQL directly in
  tests"*).
- **Parameterised test example** — `@ParameterizedTest` + `@MethodSource` pattern is shown, encouraging DDT
  adoption.

---

## Cons

### 1. Description is not "pushy" enough
The description lists coverage areas well but uses passive, reactive phrasing (*"Use when creating or writing…"*).
It should also trigger when the user says things like *"check the DB after the request"*, *"the row isn't being
saved"*, or *"add a DB assertion"*.

### 2. Code examples do not reflect full project standards
The `Example Database Test Class` section includes `@Log4j2` and `@DisplayName` but is missing:
- `@TmsLink` — mandatory per `tests/AGENTS.md`
- `AllureSteps.logStep` calls — mandatory per `component-testing` skill
- `try-finally` cleanup block — required when fixtures are inserted

A reader learning from this example will miss those requirements entirely.

### 3. Inaccurate statement about H2 reset behaviour
*"Between test classes: Database is reset (Spring caches per-class by default with `@TestInstance(PER_CLASS)`)"*
is misleading. Spring caches the context between test classes sharing the same configuration, not per-class. H2
reset between classes depends on `spring.sql.init.mode=always` re-running on each context load, which only happens
when the context is actually recreated. This needs clarification.

### 4. Reference files are mentioned only at the very end
Both `code-patterns.md` and `db-client-patterns.md` are pointed to in a two-line footer with no description of
their contents. The model has no signal about *when* to load which file, making the progressive-disclosure
architecture ineffective.

### 5. Body length is approaching the 500-line limit
At 256 lines the body is still within bounds, but many of the code snippets (especially the parameterised test and
the "empty database" snippet) could be moved to `references/code-patterns.md`, trimming the body to under 150
lines.

### 6. "Data isolation between tests" section is misleading
The comment *"No setup needed — Spring test context handles H2 reset"* and *"Each `@Test` method starts with a
clean database"* are not always true. If a prior test inserts rows without cleanup, the next test will see them.
The body should be accurate: isolation is per test class (via context lifecycle), not per method.

---

## Steps to Improve

1. **Rewrite description to be pushy** and include more trigger contexts:
   > *"… Also trigger when the user asks about database state after an API call, missing DB assertions, test data
   > cleanup, or anything involving `H2DbClient`, `JdbcTemplate`, `DbClient`, or `VideoGameTestDataFixtures` —
   > even if 'database testing' isn't mentioned explicitly."*

2. **Fix the example test class** — add `@TmsLink`, wrap assertions in `AllureSteps.logStep(...)`, and add a
   `finally` cleanup block with `dbClient.deleteVideoGameById(...)`.

3. **Correct the H2 isolation statement** — replace the misleading per-method claim with an accurate explanation:
   > *"Isolation is at the Spring context level. Tests within the same `@SpringBootTest` context share the same H2
   > instance. Always clean up inserted rows with `dbClient.deleteVideoGameById(id)` in a `finally` block."*

4. **Add a "When to load reference files" table** before the footer pointers, similar to the one in
   `spring-boot-engineer`:

   | File | Load When |
   |------|-----------|
   | `references/code-patterns.md` | Writing test methods, assertion patterns |
   | `references/db-client-patterns.md` | Implementing or extending `H2DbClient` |

5. **Move the parameterised test and "edge cases" snippets to `references/code-patterns.md`** — they are useful
   but not needed on first read. Reduce the body to the setup, fixture, and cleanup patterns (target ≈150 lines).

