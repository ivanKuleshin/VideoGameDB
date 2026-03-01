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
    "Send GET request to get all video games",
    () -> httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON));
```

## @TmsLink — single ticket

```
@Test
@TmsLink("XSP-91")
@DisplayName("...")
void myTest() { ... }
```

## @TmsLinks — multiple tickets

```
@Test
@TmsLinks({
    @TmsLink("XSP-91"),
    @TmsLink("XSP-92")
})
@DisplayName("...")
void myTest() { ... }
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
    Response response = AllureSteps.logStepAndReturn(log, "Send HTTP request", () ->
        httpClient.get(ENDPOINT.getPath(), ContentType.JSON));

    // Then
    AllureSteps.logStep(log, "Verify ...", () ->
        assertThat(response.getStatusCode()).as("...").isEqualTo(200));
}
```

