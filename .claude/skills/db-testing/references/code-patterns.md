# Database Testing Code Patterns

## Table of Contents
1. [DbClient Implementation](#dbclient-implementation)
2. [Test Fixtures](#test-fixtures)
3. [Database Assertions](#database-assertions)
4. [Parameterized Database Tests](#parameterized-database-tests)
5. [Error Handling](#error-handling)

---

## DbClient Implementation

### Basic CRUD Operations

```java
@Log4j2
@RequiredArgsConstructor
public class H2DbClient implements DbClient {

    private static final String SELECT_ALL = "SELECT * FROM VIDEOGAME";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE ID = ?";
    private static final String INSERT = "INSERT INTO VIDEOGAME VALUES (?, ?, ?, ?, ?, ?)";
    private static final String DELETE_BY_ID = "DELETE FROM VIDEOGAME WHERE ID = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<VideoGameDbModel> getAllVideoGames() {
        log.debug("Fetching all video games from DB");
        return jdbcTemplate.queryForList(SELECT_ALL)
            .stream()
            .map(this::toVideoGame)
            .toList();
    }

    @Override
    public Optional<VideoGameDbModel> getVideoGameById(int id) {
        log.debug("Fetching video game by id={} from DB", id);
        return jdbcTemplate.queryForList(SELECT_BY_ID, id)
            .stream()
            .map(this::toVideoGame)
            .findFirst();
    }

    @Override
    public void insertVideoGame(VideoGameDbModel videoGame) {
        log.debug("Inserting video game id={} into DB", videoGame.getId());
        jdbcTemplate.update(INSERT,
            videoGame.getId(),
            videoGame.getName(),
            new Date(videoGame.getReleaseDate()),
            videoGame.getReviewScore(),
            videoGame.getCategory(),
            videoGame.getRating());
    }

    @Override
    public void deleteVideoGameById(int id) {
        log.debug("Deleting video game by id={} from DB", id);
        jdbcTemplate.update(DELETE_BY_ID, id);
    }

    private VideoGameDbModel toVideoGame(Map<String, Object> row) {
        return objectMapper.convertValue(row, VideoGameDbModel.class);
    }
}
```

**Key Points**:
- Use `jdbcTemplate.queryForList()` for SELECT with List results
- Use `jdbcTemplate.update()` for INSERT/UPDATE/DELETE operations
- Use `ObjectMapper.convertValue()` for row-to-POJO mapping
- Always log at DEBUG level for troubleshooting

### Query with Type Conversion

For complex queries requiring type mapping:

```java
private VideoGameDbModel toVideoGame(Map<String, Object> row) {
    try {
        return objectMapper.convertValue(row, VideoGameDbModel.class);
    } catch (IllegalArgumentException e) {
        log.error("Failed to convert row to VideoGameDbModel", e);
        throw new RuntimeException("Database mapping failed", e);
    }
}
```

---

## Test Fixtures

### Enum-Based Fixtures

```java
@Getter
@RequiredArgsConstructor
public enum VideoGameTestDataFixtures {

    SHOOTER_GAME(101, "Doom Test", "1993-02-18", 81, "Shooter", "Mature"),
    PUZZLE_GAME(102, "Minecraft Test", "2011-12-05", 77, "Puzzle", "Universal"),
    ACTION_RPG(103, "Dark Souls Test", "2011-09-22", 89, "Action RPG", "Mature"),
    INDIE_GAME(104, "Stardew Valley Test", "2016-02-26", 85, "Simulation", "Universal");

    private final int id;
    private final String name;
    private final String releaseDateString;
    private final int reviewScore;
    private final String category;
    private final String rating;

    public VideoGameDbModel getGameData() {
        VideoGameDbModel game = new VideoGameDbModel();
        game.setId(this.id);
        game.setName(this.name);
        game.setReleaseDate(DateUtil.dateStringToEpochMillis(this.releaseDateString));
        game.setReviewScore(this.reviewScore);
        game.setCategory(this.category);
        game.setRating(this.rating);
        return game;
    }

    public VideoGameDbModel getGameDataWithId(int overrideId) {
        VideoGameDbModel game = getGameData();
        game.setId(overrideId);
        return game;
    }
}
```

### Using Fixtures in Tests

```java
@Test
void insertGameFromFixtureTest() {
    // Use fixture directly
    VideoGameDbModel game = VideoGameTestDataFixtures.SHOOTER_GAME.getGameData();
    dbClient.insertVideoGame(game);
    
    assertThat(dbClient.getVideoGameById(game.getId()))
        .isPresent();
}

@Test
void insertGameWithOverriddenIdTest() {
    // Override specific field
    VideoGameDbModel game = VideoGameTestDataFixtures.SHOOTER_GAME.getGameDataWithId(999);
    dbClient.insertVideoGame(game);
    
    assertThat(dbClient.getVideoGameById(999))
        .isPresent();
}

@Test
void insertGameWithCustomFieldTest() {
    // Override via setter
    VideoGameDbModel game = VideoGameTestDataFixtures.PUZZLE_GAME.getGameData();
    game.setReviewScore(95);
    dbClient.insertVideoGame(game);
    
    Optional<VideoGameDbModel> saved = dbClient.getVideoGameById(game.getId());
    assertThat(saved)
        .isPresent()
        .get()
        .extracting(VideoGameDbModel::getReviewScore)
        .isEqualTo(95);
}
```

---

## Database Assertions

### Verify Data After Insert

```java
@Test
@DisplayName("Insert game and verify all fields")
void insertGameWithAllFieldsTest() {
    // Given
    VideoGameDbModel expected = VideoGameTestDataFixtures.SHOOTER_GAME.getGameData();
    
    // When
    dbClient.insertVideoGame(expected);
    
    // Then
    Optional<VideoGameDbModel> actual = dbClient.getVideoGameById(expected.getId());
    assertThat(actual)
        .isPresent()
        .contains(expected);
}
```

### Verify Specific Fields

```java
@Test
@DisplayName("Verify game fields after retrieval")
void verifyGameFieldsTest() {
    // Given & When
    VideoGameDbModel game = VideoGameTestDataFixtures.ACTION_RPG.getGameData();
    dbClient.insertVideoGame(game);
    
    // Then
    Optional<VideoGameDbModel> saved = dbClient.getVideoGameById(game.getId());
    assertThat(saved)
        .isPresent()
        .get()
        .satisfies(g -> {
            assertThat(g.getName()).isEqualTo("Dark Souls Test");
            assertThat(g.getCategory()).isEqualTo("Action RPG");
            assertThat(g.getReviewScore()).isEqualTo(89);
            assertThat(g.getRating()).isEqualTo("Mature");
        });
}
```

### Verify Collection Assertions

```java
@Test
@DisplayName("Retrieve all games and verify collection")
void getAllGamesTest() {
    // Given
    dbClient.insertVideoGame(VideoGameTestDataFixtures.SHOOTER_GAME.getGameData());
    dbClient.insertVideoGame(VideoGameTestDataFixtures.PUZZLE_GAME.getGameData());
    
    // When
    List<VideoGameDbModel> allGames = dbClient.getAllVideoGames();
    
    // Then
    assertThat(allGames)
        .hasSize(2)
        .extracting(VideoGameDbModel::getId)
        .containsExactlyInAnyOrder(101, 102);
}
```

### Verify Deletion

```java
@Test
@DisplayName("Delete game and verify it's gone")
void deleteGameTest() {
    // Given
    VideoGameDbModel game = VideoGameTestDataFixtures.INDIE_GAME.getGameData();
    dbClient.insertVideoGame(game);
    
    // When
    dbClient.deleteVideoGameById(game.getId());
    
    // Then
    assertThat(dbClient.getVideoGameById(game.getId()))
        .isEmpty();
}
```

### Verify No Unintended Changes

```java
@Test
@DisplayName("Delete one game, verify others unchanged")
void deleteOneGamePreservesOthersTest() {
    // Given
    dbClient.insertVideoGame(VideoGameTestDataFixtures.SHOOTER_GAME.getGameData());
    dbClient.insertVideoGame(VideoGameTestDataFixtures.PUZZLE_GAME.getGameData());
    
    // When
    dbClient.deleteVideoGameById(VideoGameTestDataFixtures.SHOOTER_GAME.getId());
    
    // Then
    assertThat(dbClient.getVideoGameById(VideoGameTestDataFixtures.SHOOTER_GAME.getId()))
        .isEmpty();
    
    assertThat(dbClient.getVideoGameById(VideoGameTestDataFixtures.PUZZLE_GAME.getId()))
        .isPresent()
        .get()
        .extracting(VideoGameDbModel::getName)
        .isEqualTo("Minecraft Test");
}
```

---

## Parameterized Database Tests

### Using @MethodSource with Fixtures

```java
@ParameterizedTest
@DisplayName("Insert various game types")
@MethodSource("provideGameFixtures")
void insertVariousGameTypesTest(VideoGameTestDataFixtures fixture) {
    // Given
    VideoGameDbModel game = fixture.getGameData();
    
    // When
    dbClient.insertVideoGame(game);
    
    // Then
    assertThat(dbClient.getVideoGameById(game.getId()))
        .isPresent()
        .contains(game);
}

static Stream<VideoGameTestDataFixtures> provideGameFixtures() {
    return Stream.of(
        VideoGameTestDataFixtures.SHOOTER_GAME,
        VideoGameTestDataFixtures.PUZZLE_GAME,
        VideoGameTestDataFixtures.ACTION_RPG,
        VideoGameTestDataFixtures.INDIE_GAME
    );
}
```

### Using @CsvSource for Multiple Scenarios

```java
@ParameterizedTest
@CsvSource({
    "101, Doom Test, 81",
    "102, Minecraft Test, 77",
    "103, Dark Souls Test, 89"
})
@DisplayName("Verify game review scores")
void verifyReviewScoresTest(int id, String name, int expectedScore) {
    // Given
    VideoGameDbModel game = VideoGameTestDataFixtures.SHOOTER_GAME.getGameDataWithId(id);
    game.setName(name);
    game.setReviewScore(expectedScore);
    
    // When
    dbClient.insertVideoGame(game);
    
    // Then
    Optional<VideoGameDbModel> saved = dbClient.getVideoGameById(id);
    assertThat(saved)
        .isPresent()
        .get()
        .extracting(VideoGameDbModel::getReviewScore)
        .isEqualTo(expectedScore);
}
```

---

## Error Handling

### Handling Runtime Exceptions

```java
@Test
@DisplayName("Handle duplicate key insertion")
void handleDuplicateKeyTest() {
    // Given
    VideoGameDbModel game = VideoGameTestDataFixtures.SHOOTER_GAME.getGameData();
    dbClient.insertVideoGame(game);
    
    // When & Then
    assertThatThrownBy(() -> dbClient.insertVideoGame(game))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Unique constraint");
}
```

### Verifying Empty Results

```java
@Test
@DisplayName("Empty database returns no results")
void emptyDatabaseTest() {
    // When
    List<VideoGameDbModel> allGames = dbClient.getAllVideoGames();
    
    // Then
    assertThat(allGames)
        .isEmpty();
}

@Test
@DisplayName("Query non-existent id returns empty Optional")
void queryNonExistentIdTest() {
    // When
    Optional<VideoGameDbModel> game = dbClient.getVideoGameById(999);
    
    // Then
    assertThat(game)
        .isEmpty();
}
```

