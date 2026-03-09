# Code Patterns

## AllureSteps — void step

```
AllureSteps.logStep(log, "Verify response status code is 200",
    () -> assertThat(response.getStatusCode())
        .as("Response status code should be 200")
        .isEqualTo(200));
```

## AllureSteps — step with return value

```
Response response = AllureSteps.logStepAndReturn(log,
    "Send GET request to retrieve all video games",
    () -> httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON));
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
        httpClient.get(String.format(VIDEOGAME_BY_ID.getPath(), gameId), ContentType.JSON));

    // Then
    AllureSteps.logStep(log, "Verify response status code is 200", () ->
        assertThat(response.getStatusCode()).as("Status code should be 200").isEqualTo(200));
}
```

## Fixture-based test data (for DB inserts)

Always use `VideoGameTestDataFixtures` enum constants — never inline literals:

```
// ✅ Correct
Map<String, Object> body = new VideoGameBuilder()
    .withId(VideoGameTestDataFixtures.ACTION_RPG.getId())
    .withName(VideoGameTestDataFixtures.ACTION_RPG.getName())
    .build();

// ❌ Wrong — hardcoded inline data
Map<String, Object> body = new VideoGameBuilder()
    .withId(11)
    .withName("Half-Life 2")
    .build();
```

