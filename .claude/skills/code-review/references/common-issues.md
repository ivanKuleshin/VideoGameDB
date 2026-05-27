# Code Review Reference: Common Issues & Patterns

## Anti-Patterns to Identify

### 1. Improper Exception Handling

**Issue**: Using `throws` instead of handling exceptions

```java
// ❌ WRONG
public User getUser(Long id) throws SQLException {
    return dbClient.findById(id);
}

// ✅ CORRECT
public User getUser(Long id) {
    try {
        return dbClient.findById(id);
    } catch (SQLException e) {
        throw new RuntimeException("Failed to fetch user", e);
    }
}
```

**Why**: Project standards require handling exceptions with try-catch, not propagating with throws.

---

### 2. Field Injection vs Constructor Injection

**Issue**: Using field injection (@Autowired on fields)

```java
// ❌ WRONG
@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;
}

// ✅ CORRECT
@Service
public class GameService {
    private final GameRepository gameRepository;
    
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
}
```

**Why**: Constructor injection enables immutability, improves testability, and makes dependencies explicit.

---

### 3. Hardcoded Values in Tests

**Issue**: Hardcoding test data values

```java
// ❌ WRONG
@Test
void getAllGamesTest() {
    List<Game> games = httpClient.get("/games", List.class);
    assertThat(games).hasSize(10);
    assertThat(games.get(0).getId()).isEqualTo(1L);
    assertThat(games.get(0).getName()).isEqualTo("Super Mario");
}

// ✅ CORRECT
@Test
void getAllGamesTest() {
    Game expectedGame = dbClient.fetchGameById(1L);
    
    List<Game> games = AllureSteps.logStepAndReturn(log, "Fetch all games", 
        () -> httpClient.get("/games", List.class));
    
    AllureSteps.logStep(log, "Verify games list contains expected game",
        () -> assertThat(games).contains(expectedGame));
}
```

**Why**: Tests should use real test data from DB or fixtures, not hardcoded values.

---

### 4. Missing Test Structure (Given/When/Then)

**Issue**: Test without clear structure

```java
// ❌ WRONG
@Test
void testGameCreation() {
    GameRequest request = new GameRequest("New Game", 2024);
    GameResponse response = httpClient.post("/games", request, GameResponse.class);
    assertThat(response.getId()).isNotNull();
    assertThat(response.getName()).isEqualTo("New Game");
}

// ✅ CORRECT
@Test
void testGameCreationPositive() {
    // Given - prepare test data
    GameRequest request = prepareGameRequest("New Game", 2024);
    
    // When - perform action
    GameResponse response = AllureSteps.logStepAndReturn(log, "Create new game",
        () -> httpClient.post("/games", request, GameResponse.class));
    
    // Then - verify results
    AllureSteps.logStep(log, "Verify game created with correct data", () -> {
        assertThat(response.getName()).isEqualTo("New Game");
        assertThat(response.getYear()).isEqualTo(2024);
        
        Game savedGame = dbClient.fetchGameById(response.getId());
        assertThat(savedGame).isNotNull();
    });
}
```

**Why**: Clear structure improves readability, maintainability, and debugging.

---

### 5. Missing AllureSteps for Reporting

**Issue**: No step reporting

```java
// ❌ WRONG
@Test
void getAllGamesTest() {
    List<Game> games = httpClient.get("/games", List.class);
    assertThat(games).isNotEmpty();
    assertThat(games.get(0)).isNotNull();
}

// ✅ CORRECT
@Test
void getAllGamesTest() {
    List<Game> games = AllureSteps.logStepAndReturn(log, "Fetch all games",
        () -> httpClient.get("/games", List.class));
    
    AllureSteps.logStep(log, "Verify response is not empty",
        () -> assertThat(games).isNotEmpty());
    
    AllureSteps.logStep(log, "Verify first game is valid",
        () -> assertThat(games.get(0)).isNotNull());
}
```

**Why**: AllureSteps provide detailed test reporting for debugging and traceability.

---

### 6. Incorrect Naming Conventions

**Issue**: Poor naming

```java
// ❌ WRONG
public class GS { // Too abbreviated
    public void test() { } // Unclear intent
    public List<Game> get(String s) { } // Unclear parameters
    private static final String gn = "defaultName"; // Abbreviated constant
}

// ✅ CORRECT
public class GameService {
    public void validateGameData() { }
    public List<Game> findGamesByTitle(String title) { }
    private static final String DEFAULT_GAME_NAME = "defaultName";
}
```

**Why**: Clear names improve code readability and make intent obvious.

---

### 7. Magic Numbers and Strings

**Issue**: Literal values scattered throughout

```
// ❌ WRONG
if (game.getPrice() > 99.99 && game.getYear() > 2020) {
    discount = 0.15;
}

// ✅ CORRECT
private static final BigDecimal PREMIUM_GAME_PRICE = new BigDecimal("99.99");
private static final int DISCOUNT_YEAR_THRESHOLD = 2020;
private static final BigDecimal PREMIUM_DISCOUNT = new BigDecimal("0.15");

if (game.getPrice().compareTo(PREMIUM_GAME_PRICE) > 0 
    && game.getYear() > DISCOUNT_YEAR_THRESHOLD) {
    discount = PREMIUM_DISCOUNT;
}
```

**Why**: Constants improve maintainability and make values meaningful.

---

### 8. Excessive Method Length

**Issue**: Large, complex methods

```java
// ❌ WRONG - 80 lines of logic
public ResponseEntity<GameResponse> createGame(GameRequest request) {
    // 80 lines of code...
}

// ✅ CORRECT - Small, focused methods
public ResponseEntity<GameResponse> createGame(GameRequest request) {
    validateGameRequest(request);
    Game game = createGameEntity(request);
    Game savedGame = gameRepository.save(game);
    return ResponseEntity.ok(GameMapper.toResponse(savedGame));
}

private void validateGameRequest(GameRequest request) {
    // validation logic
}

private Game createGameEntity(GameRequest request) {
    // entity creation logic
}
```

**Why**: Smaller methods are easier to understand, test, and maintain.

---

### 9. Missing Input Validation

**Issue**: No request validation

```java
// ❌ WRONG
@PostMapping("/games")
public GameResponse createGame(GameRequest request) {
    Game game = new Game(request.getName(), request.getYear());
    return toResponse(gameRepository.save(game));
}

// ✅ CORRECT
@PostMapping("/games")
public GameResponse createGame(@Valid GameRequest request) {
    Game game = new Game(request.getName(), request.getYear());
    return toResponse(gameRepository.save(game));
}

@Data
class GameRequest {
    @NotBlank(message = "Game name is required")
    private String name;
    
    @Min(1900)
    @Max(2100)
    private Integer year;
}
```

**Why**: Input validation prevents invalid data and security issues.

---

### 10. Missing @TmsLink Annotations

**Issue**: Tests without Jira traceability

```java
// ❌ WRONG
@Test
void getAllGamesTest() {
    // test code
}

// ✅ CORRECT
@Test
@TmsLink("XSP-123")
@DisplayName("Should retrieve all games successfully")
void getAllGamesTest() {
    // test code
}
```

**Why**: TmsLink provides traceability between code and Jira tickets.

---

## Best Practice Patterns

### Test Fixture Usage

```java
// ✅ Use fixtures for consistent test data
enum VideoGameTestDataFixtures {
    VALID_GAME("Super Mario", 1985),
    PREMIUM_GAME("Cyberpunk 2077", 2020);
    
    private final String name;
    private final Integer year;
    
    VideoGameTestDataFixtures(String name, Integer year) {
        this.name = name;
        this.year = year;
    }
    
    public GameRequest toRequest() {
        return new GameRequest(name, year);
    }
}
```

### Database Verification in Tests

```java
// ✅ Always verify database state in component tests
@Test
void createGameTest() {
    GameRequest request = new GameRequest("New Game", 2024);
    
    GameResponse response = httpClient.post("/games", request, GameResponse.class);
    
    // Verify in database
    Game savedGame = dbClient.findById(response.getId());
    assertThat(savedGame)
        .isNotNull()
        .extracting(Game::getName, Game::getYear)
        .containsExactly("New Game", 2024);
}
```

### AssertJ Soft Assertions

```java
// ✅ Use soft assertions for multiple checks
@Test
void validateGameTest() {
    Game game = new Game("Test Game", 2024);
    
    SoftAssertions.assertSoftly(soft -> {
        soft.assertThat(game.getName()).isEqualTo("Test Game");
        soft.assertThat(game.getYear()).isEqualTo(2024);
        soft.assertThat(game.getId()).isNotNull();
    });
}
```

### Proper Lombok Usage

```java
// ✅ Use Lombok to reduce boilerplate
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private Long id;
    private String name;
    private Integer year;
}

// ✅ For tests
@Log4j2
public class GameApiTest extends ApiBaseTest {
    // Log4j2 logger automatically injected
}
```

---

## Severity Levels Guide

- **Critical**: Security issues, data loss, broken functionality
- **High**: Performance issues, test failures, incorrect logic
- **Medium**: Code quality, maintainability, code style
- **Low**: Minor improvements, code organization
- **Info**: Suggestions, best practice notes

