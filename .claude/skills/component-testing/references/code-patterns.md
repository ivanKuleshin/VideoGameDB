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
        SomeModel model = new SomeModel();
        model.setField1("value1");
        model.setField2("value2");
        return model;
    });

    // When
    Response response = AllureSteps.logStepAndReturn(log, "Send GET request to retrieve video game by ID", () ->
        apiActions.getVideoGameById(gameId, ContentType.JSON));

    // Then
    AllureSteps.logStep(log, "Verify response status code is 200", () ->
        assertThat(response.getStatusCode()).as("Status code should be 200").isEqualTo(HttpStatus.OK.value()));
}
```

## @Issue + @Disabled — linking a blocked test to its bug

Use `@Issue` from `io.qameta.allure.Issue` whenever a test is `@Disabled` due to a known app defect.
Annotation order: `@Test`, `@TmsLink`, `@Issue`, `@DisplayName`, `@Disabled`.

```
@Test
@TmsLink("XSP-105")
@Issue("XSP-139")
@DisplayName("GetVideoGameById – Non-existent ID returns 404 Not Found")
@Disabled("XSP-139: non-existent ID returns 500 instead of 404 — enable after app fix")
void getVideoGameByNonExistentIdTest() {
    // Given
    AllureSteps.logStep(log, "Verify game with ID " + NON_EXISTENT_GAME_ID + " does not exist in database",
        () -> commonSteps.verifyGameNotExistsInDatabase(log, NON_EXISTENT_GAME_ID));

    // When
    Response response = AllureSteps.logStepAndReturn(log,
        "Send GET request with non-existent ID",
        () -> apiActions.getById(NON_EXISTENT_GAME_ID, ContentType.JSON));

    // Then
    AllureSteps.logStep(log, "Verify response status code is 404 Not Found",
        () -> assertThat(response.getStatusCode())
            .as("Status code should be 404 for non-existent game ID %d", NON_EXISTENT_GAME_ID)
            .isEqualTo(HttpStatus.NOT_FOUND.value()));
}
```

## DB precondition checks in Given

Always verify DB state as an explicit Allure step — never replace with a comment.

For a seeded (existing) ID — confirm it exists:
```
AllureSteps.logStep(log, "Verify game with ID " + GAME_ID + " exists in database",
    () -> dbClient.getVideoGameById(GAME_ID).orElseThrow());
```

For a non-existing ID — wrap `commonSteps.verifyGameNotExistsInDatabase` in an AllureStep:
```
AllureSteps.logStep(log, "Verify game with ID " + NON_EXISTENT_GAME_ID + " does not exist in database",
    () -> commonSteps.verifyGameNotExistsInDatabase(log, NON_EXISTENT_GAME_ID));
```

Exception: if the path parameter is a non-integer (e.g. `"abc"`), there is no DB state to verify.
Combine Given/When into a single section comment: `// Given / When`.

## Content-Type assertions — use MediaType constants

Never hardcode media-type strings. Use `org.springframework.http.MediaType` constants:

```
// ✅ Correct
assertThat(response.getContentType()).as("...").contains(MediaType.APPLICATION_JSON_VALUE);
assertThat(response.getContentType()).as("...").contains(MediaType.APPLICATION_XML_VALUE);

// ❌ Wrong — hardcoded string
assertThat(response.getContentType()).as("...").contains("application/json");
```

## XML error response — parse via model, not raw string

Never use `.contains("<status>404</status>")` or similar raw XML fragments.
Parse the error response via `XmlUtil` into `ErrorResponseXmlModel` and assert on fields:

```
AllureSteps.logStep(log, "Verify XML error body fields match expected 404 error response",
    () -> {
        ErrorResponseXmlModel errorResponse = XmlUtil.parse(response.asString(), ErrorResponseXmlModel.class);
        assertThat(errorResponse.getStatus())
            .as("XML error response status should be 404")
            .isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.getError())
            .as("XML error response error message should be 'Not Found'")
            .isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(errorResponse.getPath())
            .as("XML error response path should match the requested resource URL")
            .isEqualTo(VIDEOGAME_PATH_PREFIX + NON_EXISTENT_GAME_ID);
    });
```

## Fixture-based test data (for DB inserts)

Always use `VideoGameTestDataFixtures` enum constants — never inline literals:

```
// ✅ Correct — full request body from fixture
Map<String, Object> body = VideoGameTestDataFixtures.ACTION_RPG.toRequestBody();

// ❌ Wrong — hardcoded inline data
Map<String, Object> body = Map.of("id", 11, "name", "Half-Life 2");
```

