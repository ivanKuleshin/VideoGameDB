# TAF Layered Architecture

## What Is It

A **Layered Test Automation Framework (TAF)** organises test code into discrete horizontal layers, each with a
single responsibility. No layer skips over the one below it — every interaction flows strictly downward. This
keeps tests readable, makes the HTTP/DB transport swappable, and removes endpoint-specific logic from test
methods entirely.

---

## Layer Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    TEST LAYER                               │
│   *ComponentTest — @Test methods, Given/When/Then only      │
├─────────────────────────────────────────────────────────────┤
│                 BUSINESS / ACTIONS LAYER                    │
│   *ApiActions   — endpoint methods, URL building, AuthType  │
├─────────────────────────────────────────────────────────────┤
│                   DRIVER / CLIENT LAYER                     │
│   HttpClient    — generic HTTP transport (get/post/put/del) │
│   H2DbClient    — generic DB access (SELECT/INSERT/DELETE)  │
├─────────────────────────────────────────────────────────────┤
│                 INFRASTRUCTURE LAYER                        │
│   Config, Fixtures, Builders, Models, Utilities             │
└─────────────────────────────────────────────────────────────┘
```

---

## Layer Responsibilities

### 1 — Test Layer (`tests/src/test/java/…/<operationName>/`)

- Contains **only** `@Test` methods structured as Given / When / Then.
- Calls `*ApiActions` methods — never `HttpClient` directly.
- Calls `dbClient` for data setup and `commonSteps` for shared DB assertions.
- Owns no URL strings, no auth logic, no `ContentType` decisions beyond what is meaningful to the scenario.

### 2 — Business / Actions Layer (`tests/src/main/java/…/client/api/`)

- One class per API operation: `GetVideoGameByIdApiActions`, `DeleteVideoGameApiActions`, etc.
- Owns the **endpoint path** (imported from `Endpoint` enum) and **per-variant auth** (`AuthType`).
- Delegates every actual HTTP call to `HttpClient`.
- Is a `@Component` — Spring-managed, injected into `*BaseTest` via `@Autowired`.
- Contains **no assertions**, **no Allure steps**, **no test logic**.

### 3 — Driver / Client Layer (`tests/src/main/java/…/client/`)

- `HttpClient` — Bill-Pugh singleton; generic `get / post / put / delete` accepting `AuthType`.  
  Knows nothing about endpoints or business operations.
- `H2DbClient` — `JdbcTemplate`-based CRUD; knows nothing about HTTP.

### 4 — Infrastructure Layer (`tests/src/main/java/…/`)

- `model/` — request/response/DB POJOs.
- `data/` — `Endpoint` enum; `VideoGameTestDataFixtures` enum (each entry holds all fields and exposes `toRequestBody()` for HTTP request bodies and `getGameData()` for DB model comparison).
- `util/`, `allure/`, `steps/` — cross-cutting utilities.

---

## Boundary Rules

| Allowed                            | Forbidden                                |
|------------------------------------|------------------------------------------|
| Test calls `apiActions.*`          | Test calls `httpClient.*` directly       |
| Test calls `dbClient.*`            | Test imports `Endpoint` for URL building |
| `*ApiActions` calls `httpClient.*` | `*ApiActions` contains assertions        |
| `*ApiActions` imports `Endpoint`   | `*ApiActions` imports `AllureSteps`      |
| `HttpClient` accepts `AuthType`    | `HttpClient` imports endpoint paths      |

---

## Project Structure

```
tests/src/main/java/com/ai/tester/
  client/
    http/
      AuthType.java                  ← DEFAULT | NONE | WRONG
      HttpClient.java                ← Driver layer: generic HTTP transport
    db/
      H2DbClient.java                ← Driver layer: JDBC access
  actions/
    api/
      get/
        byId/
          GetByIdApiActions.java     ← interface (4 get-by-id variants)
          GetVideoGameByIdApiActions.java
        getAll/
          GetAllGamesApiActions.java
      delete/
        DeleteByIdActions.java       ← interface (delete variant methods)
        DeleteVideoGameApiActions.java

tests/src/test/java/com/ai/tester/
  ApiBaseTest.java                   ← dbClient + commonSteps only
  getVideoGameById/
    GetVideoGameByIdBaseTest.java    ← @Autowired apiActions + prepare* helpers
    GetVideoGameByIdComponentTest.java ← @Test methods
```

---

## Code Examples

### Driver Layer — `HttpClient`

`HttpClient` exposes four generic verbs, each accepting an `AuthType` that selects the pre-built
`RequestSpecification`. It owns no endpoint path and no business meaning.

```java
// AuthType enum selects the spec to use
public enum AuthType { DEFAULT, NONE, WRONG }

// Generic transport — no endpoint knowledge
public Response get(String path, ContentType contentType, AuthType authType) {
    checkInitialized();
    return given(resolveSpec(authType)).accept(contentType).get(path);
}

public Response delete(String path, ContentType contentType, AuthType authType) {
    checkInitialized();
    return given(resolveSpec(authType)).accept(contentType).delete(path);
}

private RequestSpecification resolveSpec(AuthType authType) {
    return switch (authType) {
        case DEFAULT -> spec;
        case NONE -> noAuthSpec;
        case WRONG -> wrongAuthSpec;
    };
}
```

### Actions Layer — `GetVideoGameByIdApiActions`

The actions class is the only place where the endpoint path and auth variant are combined. Tests call
named methods instead of building URLs themselves.

A `private send()` method centralises the single `httpClient.get()` call — all named methods delegate
to it, so adding a new auth variant or path variant never duplicates the transport call.
Only parameterise `send()` on values that actually vary across its callers; unused parameters are a
Boat Anchor and will produce a compiler warning.

```java

@Component
@RequiredArgsConstructor
public class GetVideoGameByIdApiActions implements GetByIdApiActions {

    private final HttpClient httpClient;

    // Happy-path call — default auth
    @Override
    public Response getById(int id, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.DEFAULT);
    }

    // Auth-variant calls — same path, different AuthType
    @Override
    public Response getByIdWithoutAuth(int id, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.NONE);
    }

    @Override
    public Response getByIdWithWrongAuth(int id, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.WRONG);
    }

    // Non-standard path variant (e.g., non-integer ID scenario)
    @Override
    public Response getByInvalidId(String invalidId, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(invalidId), contentType, AuthType.DEFAULT);
    }

    // Single dispatch point — all named methods funnel through here
    private Response send(String path, ContentType contentType, AuthType authType) {
        return httpClient.get(path, contentType, authType);
    }
}
```

### Test Layer — `GetVideoGameByIdBaseTest` + `GetVideoGameByIdComponentTest`

The base test autowires the actions class and owns only `prepare*` helpers. The component test contains
only `@Test` methods — no URL strings, no `AuthType`, no `HttpClient`.

```java
// Base test — actions injection + prepare helpers
public abstract class GetVideoGameByIdBaseTest extends ApiBaseTest {

    @Autowired
    protected GetByIdApiActions apiActions;

    protected VideoGameApiModel prepareExpectedVideoGameResponse(VideoGameDbModel dbModel) {
        return VideoGameModelMapper.toApiModel(dbModel);
    }
}

// Component test — pure Given/When/Then, calls apiActions only
@Log4j2
class GetVideoGameByIdComponentTest extends GetVideoGameByIdBaseTest {

    @Test
    @TmsLinks({@TmsLink("XSP-99"), @TmsLink("XSP-100"), @TmsLink("XSP-101")})
    @DisplayName("GetVideoGameById - JSON response for existing game by ID")
    void getVideoGameByIdPositiveTest() {
        // Given
        VideoGameDbModel videoGame = AllureSteps.logStepAndReturn(log,
            "Get first video game from database", this::getFirstVideoGameFromDatabase);

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game by ID",
            () -> apiActions.getById(videoGame.getId(), ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value()));

        AllureSteps.logStep(log, "Verify response body matches the database record",
            () -> assertThat(response.as(VideoGameApiModel.class))
                .isEqualTo(prepareExpectedVideoGameResponse(videoGame)));
    }

    @Test
    @TmsLink("XSP-103")
    @DisplayName("GetVideoGameById - Request without authentication credentials")
    void getVideoGameByIdWithMissingCredentialsTest() {
        // Given
        VideoGameDbModel videoGame = AllureSteps.logStepAndReturn(log,
            "Get first video game from database", this::getFirstVideoGameFromDatabase);

        // When — auth variant expressed through the action method name, not HttpClient config
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request without authentication credentials",
            () -> apiActions.getByIdWithoutAuth(videoGame.getId(), ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
            () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }
}
```

### Cross-Endpoint Use — `DeleteVideoGameBaseTest`

When a test needs to call more than one endpoint (e.g., delete then verify via get-all), inject both
actions classes into the base test. The component test uses each by its field name.

```java
// Base test
public class DeleteVideoGameBaseTest extends ApiBaseTest {

    @Autowired
    protected DeleteVideoGameApiActions apiActions;

    @Autowired
    protected GetAllGamesApiActions getAllGamesApiActions;
}

// Component test
void deletedVideoGameAbsentFromGetAllGamesTest() {
    // ... setup ...
    AllureSteps.logStep(log, "Delete video game", () ->
        apiActions.deleteVideoGameById(SECONDARY_GAME.getId(), ContentType.JSON));

    Response getAllResponse = AllureSteps.logStepAndReturn(log, "Retrieve all video games",
        () -> getAllGamesApiActions.getAllGames(ContentType.JSON));
    // ... assertions ...
}
```

---

## Adding a New Endpoint

1. Add a `*ApiActions` class to the appropriate `tests/src/main/java/com/ai/tester/actions/api/<verb>/` package.
2. Annotate it `@Component @RequiredArgsConstructor`; inject `HttpClient` as a `private final` field.
3. Add one method per operation variant (happy path + auth variants + edge-case paths). All named methods
   delegate to a `private send()` helper that contains the single `httpClient.*` call. Only add parameters
   to `send()` for values that actually vary across callers — unused parameters are a Boat Anchor.
4. In the corresponding `*BaseTest`, add `@Autowired protected *Actions apiActions;` (autowire the interface, not the class).
5. In `*ComponentTest`, call `apiActions.*` — never `httpClient.*`.

