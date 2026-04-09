# App Refactoring Design — Spring Boot Best Practices

**Date:** 2026-04-10  
**Scope:** `app/` module only — no test changes  
**API contract:** Frozen — existing JSON/XML field names and response shapes are preserved

---

## Goals

1. Separate the input model from the JPA entity (`VideoGameRequest` DTO with validation)
2. Replace `Map<String, String>` status returns with a typed `StatusResponse`
3. Add proper HTTP error responses via `GlobalExceptionHandler` (fixes XSP-139: 500 → 404 for missing IDs)
4. Fix `updateVideoGame` ID consistency bug
5. Remove empty `WebConfig` boat anchor

---

## Architecture

The layered structure (Controller → Service → Repository) is unchanged. Three new model classes are added; one config class is deleted.

### Component Changes

| Change | Class | Purpose |
|--------|-------|---------|
| NEW | `model/VideoGameRequest` | Input DTO for POST/PUT with `@Valid` annotations |
| NEW | `model/StatusResponse` | Typed replacement for `Map<String, String>` returns |
| NEW | `model/ErrorResponse` | Error body returned by `GlobalExceptionHandler` |
| NEW | `config/GlobalExceptionHandler` | `@RestControllerAdvice` for 404/400 handling |
| MODIFY | `controller/VideoGameController` | Accept `VideoGameRequest`; return `StatusResponse` |
| MODIFY | `service/VideoGameService` | Accept `VideoGameRequest`; map to entity; fix `updateVideoGame` |
| DELETE | `app/WebConfig` | Empty class — violates no-boat-anchor rule |

`VideoGame` remains the entity **and** response model. Its JSON/XML annotations are untouched, preserving the serialized API contract. The only split is on the *input* side.

---

## New Classes

### `model/VideoGameRequest`

Input DTO for `POST` and `PUT` request bodies. Replaces direct use of the `VideoGame` entity as a request target.

```java
@Data
@XmlRootElement
public class VideoGameRequest {

    private int id;

    @NotBlank
    private String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate releaseDate;

    @Min(0) @Max(100)
    private int reviewScore;

    @NotBlank
    private String category;

    @NotBlank
    private String rating;
}
```

- `id` is not validated — on PUT it is always overwritten by the path variable; on POST the caller supplies it (no auto-generation on the entity)
- `@XmlRootElement` enables XML request body deserialization (`Content-Type: application/xml`)
- `LocalDateAdapter` is reused from `model/adapter`

### `model/StatusResponse`

Replaces `Map<String, String>` on `POST`, `DELETE`, and `DELETE /delete-even-games`. Produces identical JSON: `{"status": "..."}`.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class StatusResponse {
    private String status;
}
```

`@XmlRootElement` ensures XML serialization when `Accept: application/xml` is sent to these endpoints.

### `model/ErrorResponse`

Error body returned by `GlobalExceptionHandler`. Fields match `ErrorResponseXmlModel` in the test module exactly.

```java
@Data
@XmlRootElement
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String path;
}
```

### `config/GlobalExceptionHandler`

`@RestControllerAdvice` handling three specific exception types. No catch-all `Exception` handler — Spring's default error handling takes everything else so Spring Security and framework exceptions are unaffected.

| Exception | HTTP Status | Trigger |
|-----------|-------------|---------|
| `NoSuchElementException` | 404 | `orElseThrow()` in service when ID not found — fixes XSP-139 |
| `MethodArgumentNotValidException` | 400 | `@Valid` fails on request body |
| `MethodArgumentTypeMismatchException` | 400 | Path variable type coercion fails (e.g. `"abc"` → `int`) |

Each handler builds an `ErrorResponse` with:
- `timestamp` — current time as ISO-8601 string
- `status` — HTTP status code integer
- `error` — `HttpStatus.getReasonPhrase()`
- `path` — from `HttpServletRequest.getRequestURI()`

---

## Modified Classes

### `controller/VideoGameController`

- `createVideoGame` and `editVideoGame` accept `@Valid @RequestBody VideoGameRequest` instead of `VideoGame`
- All three status-returning methods (`createVideoGame`, `deleteVideoGame`, `deleteEvenVideoGames`) return `StatusResponse` instead of `Map<String, String>`

### `service/VideoGameService`

**New private helper** maps `VideoGameRequest` → `VideoGame` entity:

```java
private VideoGame toEntity(VideoGameRequest request) {
    VideoGame entity = new VideoGame();
    entity.setId(request.getId());
    entity.setName(request.getName());
    entity.setReleaseDate(request.getReleaseDate());
    entity.setReviewScore(request.getReviewScore());
    entity.setCategory(request.getCategory());
    entity.setRating(request.getRating());
    return entity;
}
```

**Fixed `updateVideoGame`** — always stamps path variable ID onto entity before saving; returns `save()` result directly (removes redundant second query):

```java
public VideoGame updateVideoGame(VideoGameRequest request, int videoGameId) {
    VideoGame entity = toEntity(request);
    entity.setId(videoGameId);
    return videoGameRepository.save(entity);
}
```

**`createVideoGame`** signature changes from `VideoGame` → `VideoGameRequest`:

```java
public void createVideoGame(VideoGameRequest request) {
    videoGameRepository.save(toEntity(request));
}
```

---

## Deleted Classes

### `app/WebConfig`

Empty `@Configuration` class implementing `WebMvcConfigurer` with no overrides. Spring Boot auto-configuration covers everything it was intended for. Removed per project "no boat anchor" standard.

---

## Code Style

No inline comments or Javadoc in any new or modified code — per project coding standards, code must be self-explanatory.

---

## Constraints & Non-Goals

- API contract is frozen: JSON/XML field names, response shapes, and HTTP status codes for all currently-passing scenarios are unchanged
- No changes to the `tests/` module
- No MapStruct or other mapper dependency — `toEntity()` is a plain private method in the service
- No changes to `VideoGameRepository`, `OpenApiConfig`, `SecurityConfig`, `App`, `VideoGameList`, `LocalDateAdapter`, or `schema.sql`