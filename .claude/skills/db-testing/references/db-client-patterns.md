# DbClient Development Patterns

## Table of Contents
1. [DbClient Interface Design](#dbclient-interface-design)
2. [SQL Query Patterns](#sql-query-patterns)
3. [Data Mapping](#data-mapping)
4. [Exception Handling](#exception-handling)
5. [Performance Considerations](#performance-considerations)

---

## DbClient Interface Design

### Interface Definition

```java
public interface DbClient {
    List<VideoGameDbModel> getAllVideoGames();
    Optional<VideoGameDbModel> getVideoGameById(int id);
    void insertVideoGame(VideoGameDbModel videoGame);
    void deleteVideoGameById(int id);
}
```

**Design Principles**:
- Use `Optional<T>` for single-row queries (safer than null)
- Use `List<T>` for multi-row queries (clear intent)
- Use `void` for insert/update/delete operations
- Method names reflect database operations clearly

### Interface Implementation

```java
@Log4j2
@RequiredArgsConstructor
public class H2DbClient implements DbClient {
    // ...
}
```

**Requirements**:
- `@Log4j2` annotation for logging
- `@RequiredArgsConstructor` for dependency injection
- Constructor parameters: `JdbcTemplate`, `ObjectMapper`

---

## SQL Query Patterns

### Static Query Constants

```java
private static final String SELECT_ALL = "SELECT * FROM VIDEOGAME";
private static final String SELECT_BY_ID = SELECT_ALL + " WHERE ID = ?";
private static final String INSERT = "INSERT INTO VIDEOGAME VALUES (?, ?, ?, ?, ?, ?)";
private static final String UPDATE = "UPDATE VIDEOGAME SET NAME = ?, REVIEW_SCORE = ? WHERE ID = ?";
private static final String DELETE_BY_ID = "DELETE FROM VIDEOGAME WHERE ID = ?";
```

**Benefits**:
- Centralized query definitions
- Easy to maintain
- Reusable constants
- Improves readability

### Query Execution with Parameters

```java
Optional<VideoGameDbModel> game = getVideoGameById(101);

jdbcTemplate.update(INSERT,
    videoGame.getId(),          
    videoGame.getName(),        
    videoGame.getReleaseDate(), 
    videoGame.getReviewScore(), 
    videoGame.getCategory(),    
    videoGame.getRating());     
```

**Pattern**: Parameters are substituted in order matching `?` placeholders.

### Handling Date/Timestamp Fields

```java
new Date(videoGame.getReleaseDate())
```

---

## Data Mapping

### Row-to-POJO Conversion

```java
private VideoGameDbModel toVideoGame(Map<String, Object> row) {
    return objectMapper.convertValue(row, VideoGameDbModel.class);
}
```

**How it works**:
1. `jdbcTemplate.queryForList()` returns `List<Map<String, Object>>`
2. Each map represents one row with column names as keys
3. `ObjectMapper.convertValue()` maps the map to POJO

**Example row map**:
```
{
  "ID": 101,
  "NAME": "Doom Test",
  "RELEASE_DATE": 731704000000,
  "REVIEW_SCORE": 81,
  "CATEGORY": "Shooter",
  "RATING": "Mature"
}
```

### Using in Queries

```java
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
```

### POJO Model Requirements

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoGameDbModel {
    private Integer id;
    private String name;
    private Long releaseDate;
    private Integer reviewScore;
    private String category;
    private String rating;
}
```

**Key**: Use wrapper types (Integer, Long) instead of primitives to handle nulls gracefully.

---

## Exception Handling

### DbClient Exception Pattern

```java
@Override
public Optional<VideoGameDbModel> getVideoGameById(int id) {
    try {
        log.debug("Fetching video game by id={} from DB", id);
        return jdbcTemplate.queryForList(SELECT_BY_ID, id)
            .stream()
            .map(this::toVideoGame)
            .findFirst();
    } catch (DataAccessException e) {
        log.error("Database error fetching game id={}", id, e);
        throw new RuntimeException("Failed to fetch video game from database", e);
    }
}
```

**Rule**: Never use `throws` - always catch and convert to `RuntimeException`.

### Mapping Exception Pattern

```java
private VideoGameDbModel toVideoGame(Map<String, Object> row) {
    try {
        return objectMapper.convertValue(row, VideoGameDbModel.class);
    } catch (IllegalArgumentException e) {
        log.error("Failed to convert database row to VideoGameDbModel: {}", row, e);
        throw new RuntimeException("Database mapping failed", e);
    }
}
```

### Test Exception Handling

```java
@Test
@DisplayName("Handle database errors gracefully")
void handleDatabaseErrorTest() {
    // When & Then
    assertThatThrownBy(() -> dbClient.getVideoGameById(999))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Failed to fetch");
}
```

---

## Performance Considerations

### Batch Operations

For inserting multiple records, consider batch updates:

```java
public void insertMultipleVideoGames(List<VideoGameDbModel> games) {
    for (VideoGameDbModel game : games) {
        insertVideoGame(game);
    }
}
```

**Alternative - using batchUpdate**:
```java
public void insertMultipleVideoGames(List<VideoGameDbModel> games) {
    List<Object[]> batchArgs = games.stream()
        .map(g -> new Object[]{
            g.getId(),
            g.getName(),
            new Date(g.getReleaseDate()),
            g.getReviewScore(),
            g.getCategory(),
            g.getRating()
        })
        .toList();
    
    jdbcTemplate.batchUpdate(INSERT, batchArgs);
}
```

### Query Optimization

```java
// ❌ Inefficient - retrieves all columns
private static final String SELECT_ALL = "SELECT * FROM VIDEOGAME";

// ✅ Better - select only needed columns (if applicable)
private static final String SELECT_ALL = 
    "SELECT ID, NAME, REVIEW_SCORE FROM VIDEOGAME";
```

### Logging Guidelines

```java
// Use DEBUG level for routine operations
log.debug("Fetching video game by id={} from DB", id);

// Use ERROR level for exceptions
log.error("Database error fetching game id={}", id, exception);
```

---

## Complete DbClient Example

```java
@Log4j2
@RequiredArgsConstructor
public class H2DbClient implements DbClient {

    private static final String SELECT_ALL = "SELECT * FROM VIDEOGAME";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE ID = ?";
    private static final String INSERT = "INSERT INTO VIDEOGAME VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE VIDEOGAME SET NAME = ? WHERE ID = ?";
    private static final String DELETE_BY_ID = "DELETE FROM VIDEOGAME WHERE ID = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<VideoGameDbModel> getAllVideoGames() {
        try {
            log.debug("Fetching all video games from DB");
            return jdbcTemplate.queryForList(SELECT_ALL)
                .stream()
                .map(this::toVideoGame)
                .toList();
        } catch (DataAccessException e) {
            log.error("Database error fetching all games", e);
            throw new RuntimeException("Failed to fetch video games from database", e);
        }
    }

    @Override
    public Optional<VideoGameDbModel> getVideoGameById(int id) {
        try {
            log.debug("Fetching video game by id={} from DB", id);
            return jdbcTemplate.queryForList(SELECT_BY_ID, id)
                .stream()
                .map(this::toVideoGame)
                .findFirst();
        } catch (DataAccessException e) {
            log.error("Database error fetching game id={}", id, e);
            throw new RuntimeException("Failed to fetch video game from database", e);
        }
    }

    @Override
    public void insertVideoGame(VideoGameDbModel videoGame) {
        try {
            log.debug("Inserting video game id={} into DB", videoGame.getId());
            jdbcTemplate.update(INSERT,
                videoGame.getId(),
                videoGame.getName(),
                new Date(videoGame.getReleaseDate()),
                videoGame.getReviewScore(),
                videoGame.getCategory(),
                videoGame.getRating());
        } catch (DataAccessException e) {
            log.error("Database error inserting game id={}", videoGame.getId(), e);
            throw new RuntimeException("Failed to insert video game into database", e);
        }
    }

    @Override
    public void deleteVideoGameById(int id) {
        try {
            log.debug("Deleting video game by id={} from DB", id);
            jdbcTemplate.update(DELETE_BY_ID, id);
        } catch (DataAccessException e) {
            log.error("Database error deleting game id={}", id, e);
            throw new RuntimeException("Failed to delete video game from database", e);
        }
    }

    private VideoGameDbModel toVideoGame(Map<String, Object> row) {
        try {
            return objectMapper.convertValue(row, VideoGameDbModel.class);
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert database row to VideoGameDbModel: {}", row, e);
            throw new RuntimeException("Database mapping failed", e);
        }
    }
}
```

