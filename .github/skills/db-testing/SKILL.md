---
name: db-testing
description: >-
  Guide for implementing database testing in Spring Boot applications using H2 in-memory database and JdbcTemplate.
  Use when creating or writing database-layer tests, test data fixtures, database assertions, and DB client tests.
  Also trigger when the user asks about database state after an API call, missing DB assertions, test data cleanup,
  a row not being saved or deleted, or anything involving H2DbClient, JdbcTemplate, DbClient, or
  VideoGameTestDataFixtures — even if "database testing" is not mentioned explicitly.
  Covers: H2 database setup, DbClient implementation, test data fixtures, database state verification, and cleanup strategies.
---

# Database Testing Guide

## Overview

This skill provides patterns for testing database interactions in Spring Boot applications using:

- **H2** in-memory database for fast, isolated tests
- **JdbcTemplate** for SQL execution and queries
- **Test fixtures** via enum-based patterns for reusable test data
- **DbClient** abstraction for database operations

## Setup & Configuration

### H2 Database Configuration

H2 is automatically configured via `application-test.properties`:

- Uses in-memory mode with automatic schema creation
- Isolation is at the **Spring context level** — tests within the same `@SpringBootTest` context share the same
  H2 instance. H2 is reset only when the Spring context is recreated (e.g., between test classes with different
  configurations).
- **Always clean up inserted rows** with `dbClient.deleteVideoGameById(id)` in a `finally` block — do not rely
  on automatic reset between individual test methods.

### DbClient Pattern

All database access flows through a `DbClient` interface with `H2DbClient` implementation:

```java
public interface DbClient {
    List<VideoGameDbModel> getAllVideoGames();

    Optional<VideoGameDbModel> getVideoGameById(int id);

    void insertVideoGame(VideoGameDbModel videoGame);

    void deleteVideoGameById(int id);
}
```

**Why**: Allows easy mocking, provides consistent query execution, centralizes SQL logic.

## Test Data Management: Fixtures Pattern

Use **enum-based fixtures** for clean, reusable test data:

```java

@Getter
@RequiredArgsConstructor
public enum VideoGameTestDataFixtures {
    SHOOTER_GAME(101, "Doom Test", "1993-02-18", 81, "Shooter", "Mature"),
    PUZZLE_GAME(102, "Minecraft Test", "2011-12-05", 77, "Puzzle", "Universal");

    private final int id;
    private final String name;
    private final String releaseDateString;
    // ... other fields

    public VideoGameDbModel getGameData() {
        VideoGameDbModel game = new VideoGameDbModel();
        game.setId(this.id);
        // ... populate fields
        return game;
    }

    public VideoGameDbModel getGameDataWithId(int overrideId) {
        VideoGameDbModel game = getGameData();
        game.setId(overrideId);
        return game;
    }
}
```

**Benefits**:

- Eliminates test data boilerplate
- Centralized data definitions
- Easy to override specific fields (e.g., `getGameDataWithId(999)`)
- Self-documenting test scenarios

## Database Assertions Pattern

### Verify Database State Changes

After an operation, verify the database was modified correctly:

```

@Test
@DisplayName("Insert video game into database")
void insertVideoGameTest() {
    // Given
    VideoGameDbModel expectedGame = VideoGameTestDataFixtures.SHOOTER_GAME.getGameData();

    // When
    dbClient.insertVideoGame(expectedGame);

    // Then
    Optional<VideoGameDbModel> savedGame = dbClient.getVideoGameById(expectedGame.getId());
    assertThat(savedGame)
        .isPresent()
        .contains(expectedGame);
}
```

### Verify No Unwanted Changes

Check that unrelated data remains unchanged:

```java

@Test
@DisplayName("Delete only specified game, keep others")
void deleteVideoGameTest() {
    // Given
    dbClient.insertVideoGame(VideoGameTestDataFixtures.SHOOTER_GAME.getGameData());
    dbClient.insertVideoGame(VideoGameTestDataFixtures.PUZZLE_GAME.getGameData());

    // When
    dbClient.deleteVideoGameById(SHOOTER_GAME.getId());

    // Then - verify deletion
    assertThat(dbClient.getVideoGameById(SHOOTER_GAME.getId()))
        .isEmpty();

    // Then - verify other data untouched
    assertThat(dbClient.getVideoGameById(PUZZLE_GAME.getId()))
        .isPresent();
}
```

## Test Class Structure

All database tests inherit from `ApiBaseTest` which provides:

- `@Autowired DbClient dbClient` — for database operations
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` — with H2 context
- `@ActiveProfiles("test")` — uses test database configuration

### Example Database Test Class

```java

@Log4j2
@DisplayName("Database Operations Tests")
class VideoGameDbOperationsComponentTest extends ApiBaseTest {

    @Test
    @TmsLink("XSP-123")
    @DisplayName("Insert video game and verify it is persisted")
    void insertVideoGameTest() {
        VideoGameDbModel game = VideoGameTestDataFixtures.SHOOTER_GAME.getGameData();
        try {
            // Given
            AllureSteps.logStep(log, "Insert test fixture into database",
                () -> dbClient.insertVideoGame(game));

            // Then
            AllureSteps.logStep(log, "Verify game is persisted in database", () ->
                assertThat(dbClient.getVideoGameById(game.getId()))
                    .isPresent()
                    .contains(game));
        } finally {
            dbClient.deleteVideoGameById(game.getId());
        }
    }
}
```

## Best Practices

1. **Use fixtures for all test data** — eliminates magic numbers and repeated initialization
2. **Test in Given/When/Then structure** — clearly separates setup, action, and verification
3. **Verify both positive and negative outcomes** — test what was added AND what wasn't removed
4. **Use `Optional` assertions** for single-row lookups — clearer intent than null checks
5. **Leverage DbClient abstraction** — never write SQL directly in tests
6. **Always clean up in a `finally` block** — isolation is per context, not per method; rows inserted in one
   test will be visible in the next unless explicitly deleted

## Database Cleanup Strategy

- **Automatic**: Spring's test context manages H2 lifecycle — H2 is reset when a new context is created
- **Between test classes**: Context may be reused if the configuration is identical; do not assume a clean slate
- **Within test class**: Always wrap insert + assert in a `try-finally` and delete the row in the `finally` block:

```
VideoGameDbModel game = VideoGameTestDataFixtures.SHOOTER_GAME.getGameData();
try{
    dbClient.

insertVideoGame(game);
// ... assertions
}finally{
    dbClient.

deleteVideoGameById(game.getId());
    }
```

---

## Reference Files

| File                               | Load When                                                                 |
|------------------------------------|---------------------------------------------------------------------------|
| `references/code-patterns.md`      | Writing test methods, assertion patterns, parameterized tests, edge cases |
| `references/db-client-patterns.md` | Implementing or extending `H2DbClient`, SQL query patterns                |
