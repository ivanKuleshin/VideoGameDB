---
name: db-testing
description: >-
  Guide for implementing database testing in Spring Boot applications using H2 in-memory database and JdbcTemplate.
  Use when creating or writing database-layer tests, test data fixtures, database assertions, and DB client tests.
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
- Isolation: Each test class gets its own isolated database state
- Cleanup: Database is reset between test classes (via Spring's test context caching)

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

```java
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
    @DisplayName("Retrieve all games from database")
    void getAllVideoGamesTest() {
        // Given
        dbClient.insertVideoGame(VideoGameTestDataFixtures.SHOOTER_GAME.getGameData());
        dbClient.insertVideoGame(VideoGameTestDataFixtures.PUZZLE_GAME.getGameData());

        // When
        List<VideoGameDbModel> allGames = dbClient.getAllVideoGames();

        // Then
        assertThat(allGames)
            .hasSize(2)
            .extracting(VideoGameDbModel::getName)
            .containsExactly("Doom Test", "Minecraft Test");
    }
}
```

## Common Patterns

### Testing with Multiple Fixture Variants

Use `@MethodSource` with fixtures for parameterized tests:

```java
@ParameterizedTest
@DisplayName("Insert various game types")
@MethodSource("gameFixtures")
void insertVariousGamesTest(VideoGameTestDataFixtures fixture) {
    // Given
    VideoGameDbModel game = fixture.getGameData();
    
    // When
    dbClient.insertVideoGame(game);
    
    // Then
    assertThat(dbClient.getVideoGameById(game.getId()))
        .isPresent()
        .contains(game);
}

static Stream<VideoGameTestDataFixtures> gameFixtures() {
    return Stream.of(
        VideoGameTestDataFixtures.SHOOTER_GAME,
        VideoGameTestDataFixtures.PUZZLE_GAME,
        VideoGameTestDataFixtures.ACTION_RPG,
        VideoGameTestDataFixtures.INDIE_GAME
    );
}
```

### Testing Edge Cases

**Empty database query**:
```java
@Test
@DisplayName("Get game by id when database is empty")
void getGameByIdEmptyDatabaseTest() {
    assertThat(dbClient.getVideoGameById(999))
        .isEmpty();
}
```

**Data isolation between tests**:
- No setup needed — Spring test context handles H2 reset
- Each `@Test` method starts with clean database

### Updating Existing Fixtures

When you need to modify a fixture field (e.g., different review score):

```java
VideoGameDbModel customGame = VideoGameTestDataFixtures.SHOOTER_GAME.getGameData();
customGame.setReviewScore(95);  // Override specific field
dbClient.insertVideoGame(customGame);
```

Or use `getGameDataWithId()` for ID overrides:

```java
VideoGameDbModel gameWithDifferentId = 
    VideoGameTestDataFixtures.SHOOTER_GAME.getGameDataWithId(999);
```

## Best Practices

1. **Use fixtures for all test data** — eliminates magic numbers and repeated initialization
2. **Test in Given/When/Then structure** — clearly separates setup, action, and verification
3. **Verify both positive and negative outcomes** — test what was added AND what wasn't removed
4. **Use `Optional` assertions** for single-row lookups — clearer intent than null checks
5. **Leverage DbClient abstraction** — never write SQL directly in tests
6. **Keep tests independent** — H2 reset between tests ensures isolation

## Database Cleanup Strategy

- **Automatic**: Spring's test context manages H2 lifecycle
- **Between test classes**: Database is reset (Spring caches per-class by default with `@TestInstance(PER_CLASS)`)
- **Within test class**: Use DbClient methods to clean up specific data if needed:

```java
@AfterEach
void cleanupSpecificData() {
    dbClient.deleteVideoGameById(VideoGameTestDataFixtures.SHOOTER_GAME.getId());
}
```

---

For implementation patterns and code examples, see [code-patterns.md](references/code-patterns.md).

For DbClient development and SQL patterns, see [db-client-patterns.md](references/db-client-patterns.md).

