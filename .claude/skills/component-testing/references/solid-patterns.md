# SOLID Principles in the TAF

Each principle is explained in the context of this framework, with concrete before/after examples drawn
from the `GetVideoGameById` operation — the canonical reference for how all new operations must be built.

---

## S — Single Responsibility Principle

> Every class has exactly one reason to change.

The framework enforces SRP by splitting each concern into its own class:

| Class | Single responsibility |
|---|---|
| `HttpClient` | Raw HTTP transport — `get / post / put / delete` with `AuthType` |
| `GetVideoGameByIdApiActions` | HTTP actions for one endpoint — path building, auth variant selection |
| `GetVideoGameByIdBaseTest` | `prepare*` helpers and shared constants for one endpoint's tests |
| `GetVideoGameByIdComponentTest` | `@Test` methods only — no helpers, no HTTP wiring |

**❌ Wrong — actions class mixes HTTP and assertion logic:**

```java
// SRP violation: ApiActions should NEVER assert or log Allure steps
public Response getVideoGameById(int id, ContentType contentType) {
    Response response = httpClient.get(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.DEFAULT);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value()); // ← belongs in the test
    return response;
}
```

**✅ Correct — actions class is transport only, uses private send() to avoid repeating httpClient calls:**

```java
// actions/api/get/byId/GetVideoGameByIdApiActions.java
@Override
public Response getById(int id, ContentType contentType) {
    return send(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.DEFAULT);
}

private Response send(String path, ContentType contentType, AuthType authType) {
    return httpClient.get(path, contentType, authType);
}
```

---

## O — Open/Closed Principle

> Open for extension, closed for modification.

**Adding a new auth variant** — extend `GetVideoGameByIdApiActions` with a new method; existing methods
are untouched:

```java
// ✅ Extend: add a method, change nothing
public Response getVideoGameByIdWithExpiredToken(int id, ContentType contentType) {
    return httpClient.get(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.EXPIRED);
}
```

**Adding a new `AuthType`** — add an enum constant; the `switch` in `HttpClient.resolveSpec()` is the
only place that changes, and it's exhaustive so the compiler forces you to handle the new case:

```java
// AuthType.java — add a constant; HttpClient.resolveSpec() is the single change point
public enum AuthType { DEFAULT, NONE, WRONG, EXPIRED }
```

**Adding a new endpoint** — create a new `*Actions` interface + `*ApiActions` class; nothing else
is modified:

```java
// ✅ New endpoint = new files only, zero changes to existing classes
public interface PutVideoGameActions { ... }

@Component @RequiredArgsConstructor
public class PutVideoGameApiActions implements PutVideoGameActions {
    private final HttpClient httpClient;
    // ...
}
```

**❌ Wrong — modifying an existing class to add new endpoint behaviour:**

```java
// OCP violation: adding a PUT method to GetVideoGameByIdApiActions
public Response updateVideoGame(int id, Object body, ContentType contentType) { ... }
```

---

## L — Liskov Substitution Principle

> Any implementation of an interface must honour the full contract — no surprises, no silent no-ops.

`GetVideoGameByIdApiActions` implements `GetVideoGameByIdActions`. Any future replacement (e.g., a mock
for isolated unit testing) must:
- Return a `Response` for every method — never `null`.
- Not throw `UnsupportedOperationException` for any declared method.
- Preserve the semantics: `getVideoGameByIdWithoutAuth` must actually send a request without credentials.

**❌ Wrong — partial implementation breaks substitutability:**

```java
// LSP violation: caller can no longer safely substitute this for GetByIdApiActions
public class MockVideoGameByIdActions implements GetByIdApiActions {
    public Response getById(int id, ContentType contentType) { return mockResponse; }
    public Response getByIdWithoutAuth(int id, ContentType contentType) {
        throw new UnsupportedOperationException(); // ← breaks contract
    }
    // ...
}
```

**✅ Correct — every implementation fully satisfies the contract:**

```java
public class GetVideoGameByIdApiActions implements GetByIdApiActions {

    public Response getById(int id, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.DEFAULT);
    }

    public Response getByIdWithoutAuth(int id, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.NONE);
    }

    public Response getByIdWithWrongAuth(int id, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.WRONG);
    }

    public Response getByInvalidId(String invalidId, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(invalidId), contentType, AuthType.DEFAULT);
    }

    private Response send(String path, ContentType contentType, AuthType authType) {
        return httpClient.get(path, contentType, authType);
    }
}
```

---

## I — Interface Segregation Principle

> Clients should not be forced to depend on methods they do not use.

Each endpoint has its own **narrow interface** that declares only the HTTP actions relevant to that
endpoint. The test class depends on `GetVideoGameByIdActions`, which contains only the four
"get by ID" operations — nothing from delete, put, or any other endpoint.

**❌ Wrong — one fat interface forces every test to depend on all endpoints:**

```java
// ISP violation: a base test for GetVideoGameById should not need delete or put methods
public interface AllApiActions {
    Response getVideoGameById(int id, ContentType contentType);
    Response deleteVideoGameById(int id, ContentType contentType);
    Response updateVideoGame(int id, Object body, ContentType contentType);
    // ...
}
```

**✅ Correct — one focused interface per endpoint:**

```java
u// actions/api/get/byId/GetByIdApiActions.java
public interface GetByIdApiActions {

    Response getById(int id, ContentType contentType);

    Response getByIdWithoutAuth(int id, ContentType contentType);

    Response getByIdWithWrongAuth(int id, ContentType contentType);

    Response getByInvalidId(String invalidId, ContentType contentType);
}
```

When a test needs two endpoints (e.g., delete then verify via get-all), inject **two separate interfaces**
into the base test — do not merge them:

```java
// deleteVideoGame/DeleteVideoGameBaseTest.java
public class DeleteVideoGameBaseTest extends ApiBaseTest {

    @Autowired
    protected DeleteByIdActions apiActions;          // ← only delete methods

    @Autowired
    protected GetAllGamesApiActions getAllGamesApiActions; // ← only get-all methods
}
```

---

## D — Dependency Inversion Principle

> High-level modules must not depend on low-level modules. Both must depend on abstractions.

This is the most critical principle for keeping tests decoupled from implementation details.

**Rule:** every `*BaseTest` must autowire the **interface**, never the concrete `*ApiActions` class.

**❌ Wrong — base test depends on the concrete class:**

```java
// DIP violation: if the implementation changes, this import and field type must change too
 import com.ai.tester.actions.api.get.byId.GetVideoGameByIdApiActions;

@Autowired
protected GetVideoGameByIdApiActions apiActions; // ← concrete, not abstract
```

**✅ Correct — base test depends on the interface:**

```java
// getVideoGameById/GetVideoGameByIdBaseTest.java
import com.ai.tester.actions.api.get.byId.GetByIdApiActions;

@Autowired
protected GetByIdApiActions apiActions; // ← abstraction
```

Spring resolves the single `@Component` implementation (`GetVideoGameByIdApiActions`) automatically.
If a second implementation is ever added (e.g., for a mocked environment), the test class requires
**zero changes** — only the Spring wiring changes.

---

## Summary — Rules for Adding a New Endpoint

1. **Create `*Actions` interface** — one method per operation variant (default auth + auth variants +
   edge-case paths). No assertions, no Allure steps, no Spring annotations.
2. **Create `*ApiActions` class** — `@Component @RequiredArgsConstructor`, implements the interface,
   holds `private final HttpClient httpClient`. All named methods delegate to a **`private send()`**
   helper that contains the single `httpClient.*` call. Only add parameters to `send()` for values that
   actually vary across callers — an always-constant parameter is a Boat Anchor.
3. **`*BaseTest` autowires the interface**, not the concrete class. The field is `protected` so the
   component test inherits access.
4. **`*ComponentTest` calls `apiActions.*`** — never imports `HttpClient`, `AuthType`, or `Endpoint`.

```
GetByIdApiActions                  ← interface (ISP, DIP)
    └── GetVideoGameByIdApiActions      ← @Component impl (SRP, LSP, OCP)
            private send(...)           ← single httpClient.get() call point
            └── HttpClient              ← injected via constructor (DIP)

GetVideoGameByIdBaseTest
    @Autowired GetByIdApiActions apiActions  ← depends on interface (DIP)

GetVideoGameByIdComponentTest extends GetVideoGameByIdBaseTest
    apiActions.getById(...)    ← no HTTP details visible (SRP)
```

