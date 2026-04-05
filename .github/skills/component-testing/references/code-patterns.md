# Code Patterns

## Class Member Ordering

All classes must follow Google Checkstyle member ordering (`ModifierOrder` rule):

```java
public final class HttpClient {

    private static final class Holder { ... }          // 1. static nested class

    private RequestSpecification spec;                  // 2. instance fields
    private RequestSpecification noAuthSpec;
    private RequestSpecification wrongAuthSpec;

    private HttpClient() { }                            // 3. constructor

    public static HttpClient getInstance() { ... }     // 4. public static method

    public void init(...) { ... }                       // 5. public instance methods
    public Response get(...) { ... }
    public Response post(...) { ... }
    public Response put(...) { ... }
    public Response delete(...) { ... }

    private static PrintStream createLog4jPrintStream() { ... }    // 6. private static methods
    private static RequestSpecification createAuthSpec(...) { ... }
    private static RequestSpecification createNoAuthSpec(...) { ... }

    private void checkInitialized() { ... }            // 7. private instance methods
    private RequestSpecification resolveSpec(...) { ... }
}
```

## AllureSteps — void step

```
AllureSteps.logStep(log, "Verify response status code is 200",
    () -> assertThat(response.getStatusCode())
        .as("Response status code should be 200")
        .isEqualTo(HttpStatus.OK.value()));
```

## AllureSteps — step with return value

```
Response response = AllureSteps.logStepAndReturn(log,
    "Send GET request to retrieve all video games",
    () -> apiActions.getAllGames(ContentType.JSON));
```

## @TmsLink — single ticket

```
@Test
@TmsLink("XSP-91")
@DisplayName("...")
void myTest() { ... }
```

## @TmsLinks — multiple tickets covering the same scenario

Use a **single test method** with `@TmsLinks` when multiple tickets describe the same operation
(e.g., "returns 200" and "record is persisted" both test the same POST call):

```
@Test
@TmsLinks({
    @TmsLink("XSP-108"),
    @TmsLink("XSP-109")
})
@DisplayName("Video game creation returns 200 and persists the record")
void postVideoGameCreatesRecordTest() {
    // Given / When / Then covering both ACs in one flow
}
```

## AssertJ — always include `.as()` message

```
assertThat(actual)
    .as("Descriptive failure message")
    .isEqualTo(expected);
```

## Given/When/Then structure

```
void myTest() {
    // Given
    SomeModel data = AllureSteps.logStepAndReturn(log, "Prepare test data", () -> {
        // setup and return
    });

    // When
    Response response = AllureSteps.logStepAndReturn(log, "Send GET request to retrieve video game by ID", () ->
        apiActions.getVideoGameById(gameId, ContentType.JSON));

    // Then
    AllureSteps.logStep(log, "Verify response status code is 200", () ->
        assertThat(response.getStatusCode()).as("Status code should be 200").isEqualTo(HttpStatus.OK.value()));
}
```

## Fixture-based test data (for DB inserts)

Always use `VideoGameTestDataFixtures` enum constants — never inline literals:

```
// ✅ Correct — full request body from fixture
Map<String, Object> body = VideoGameTestDataFixtures.ACTION_RPG.toRequestBody();

// ✅ Correct — id-only request body from fixture
Map<String, Object> body = new HashMap<>();
body.put("id", VideoGameTestDataFixtures.POST_ID_ONLY_GAME.getId());

// ❌ Wrong — hardcoded inline data
Map<String, Object> body = Map.of("id", 11, "name", "Half-Life 2");
```

