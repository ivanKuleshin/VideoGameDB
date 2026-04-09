# App Refactoring Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor the `app/` module to add a `VideoGameRequest` input DTO with validation, typed status/error response models, and a global exception handler — without changing the API contract.

**Architecture:** `VideoGame` stays as both entity and response model. Three new model classes are added (`VideoGameRequest`, `StatusResponse`, `ErrorResponse`) alongside a `GlobalExceptionHandler`. The service maps `VideoGameRequest` → `VideoGame` entity internally via a private `toEntity` helper.

**Tech Stack:** Java 21, Spring Boot 3.5.3, Spring MVC, Spring Data JPA, Hibernate Validator (`spring-boot-starter-validation`), Jackson XML (`jackson-dataformat-xml`), Lombok

---

## File Map

| Action | File |
|--------|------|
| MODIFY | `app/pom.xml` |
| DELETE | `app/src/main/java/com/ai/tester/app/WebConfig.java` |
| CREATE | `app/src/main/java/com/ai/tester/model/VideoGameRequest.java` |
| CREATE | `app/src/main/java/com/ai/tester/model/StatusResponse.java` |
| CREATE | `app/src/main/java/com/ai/tester/model/ErrorResponse.java` |
| CREATE | `app/src/main/java/com/ai/tester/config/GlobalExceptionHandler.java` |
| MODIFY | `app/src/main/java/com/ai/tester/service/VideoGameService.java` |
| MODIFY | `app/src/main/java/com/ai/tester/controller/VideoGameController.java` |

---

## Task 1: Add Validation Dependency

`@NotBlank`, `@Min`, `@Max`, `@NotNull` require `hibernate-validator` at runtime. `spring-boot-starter-web` does not include it — `spring-boot-starter-validation` does.

**Files:**
- Modify: `app/pom.xml`

- [ ] **Step 1: Add dependency**

In `app/pom.xml`, insert after the `spring-boot-starter-security` dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

- [ ] **Step 2: Build**

```bash
mvn install -pl app -DskipTests
```

Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add app/pom.xml
git commit -m "feat: add spring-boot-starter-validation dependency"
```

---

## Task 2: Delete WebConfig

`WebConfig` implements `WebMvcConfigurer` but overrides nothing. Spring Boot auto-configuration covers everything it was intended for.

**Files:**
- Delete: `app/src/main/java/com/ai/tester/app/WebConfig.java`

- [ ] **Step 1: Delete the file**

```bash
rm app/src/main/java/com/ai/tester/app/WebConfig.java
```

- [ ] **Step 2: Build**

```bash
mvn install -pl app -DskipTests
```

Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ai/tester/app/WebConfig.java
git commit -m "refactor: remove empty WebConfig boat anchor"
```

---

## Task 3: Create VideoGameRequest

Input DTO for `POST` and `PUT` request bodies. Validation annotations enforce data integrity. XML annotations must produce root element `<videoGame>` to match the XML format the test suite sends (`PostVideoGameXmlRequestModel` and `UpdateVideoGameXmlRequestModel` both use `localName = "videoGame"`).

**Files:**
- Create: `app/src/main/java/com/ai/tester/model/VideoGameRequest.java`

- [ ] **Step 1: Create the file**

```java
package com.ai.tester.model;

import com.ai.tester.model.adapter.LocalDateAdapter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import lombok.Data;

@Data
@XmlRootElement(name = "videoGame")
@JacksonXmlRootElement(localName = "videoGame")
public class VideoGameRequest {

    private int id;

    @NotBlank
    private String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate releaseDate;

    @Min(0)
    @Max(100)
    private int reviewScore;

    @NotBlank
    private String category;

    @NotBlank
    private String rating;
}
```

- [ ] **Step 2: Build**

```bash
mvn install -pl app -DskipTests
```

Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ai/tester/model/VideoGameRequest.java
git commit -m "feat: add VideoGameRequest input DTO with validation"
```

---

## Task 4: Create StatusResponse

Typed replacement for `Map<String, String>` returned by `POST`, `DELETE /id`, and `DELETE /delete-even-games`. Produces identical JSON: `{"status": "..."}`. `@NoArgsConstructor` is required alongside `@AllArgsConstructor` so JAXB can marshal the object to XML.

**Files:**
- Create: `app/src/main/java/com/ai/tester/model/StatusResponse.java`

- [ ] **Step 1: Create the file**

```java
package com.ai.tester.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@JacksonXmlRootElement(localName = "statusResponse")
public class StatusResponse {
    private String status;
}
```

- [ ] **Step 2: Build**

```bash
mvn install -pl app -DskipTests
```

Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ai/tester/model/StatusResponse.java
git commit -m "feat: add StatusResponse to replace Map<String,String> returns"
```

---

## Task 5: Create ErrorResponse

Body returned by `GlobalExceptionHandler` for 404 and 400 responses. The root element **must** be `<Map>` — this is what `ErrorResponseXmlModel` in the test suite expects (`@JacksonXmlRootElement(localName = "Map")`). Fields must exactly match: `timestamp`, `status`, `error`, `path`.

**Files:**
- Create: `app/src/main/java/com/ai/tester/model/ErrorResponse.java`

- [ ] **Step 1: Create the file**

```java
package com.ai.tester.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@JacksonXmlRootElement(localName = "Map")
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String path;
}
```

- [ ] **Step 2: Build**

```bash
mvn install -pl app -DskipTests
```

Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ai/tester/model/ErrorResponse.java
git commit -m "feat: add ErrorResponse model for exception handler"
```

---

## Task 6: Create GlobalExceptionHandler

`@RestControllerAdvice` handling three exception types. Returns `ResponseEntity<ErrorResponse>` so the HTTP status and body are set explicitly. No catch-all handler — Spring Security and framework exceptions continue to be handled by Spring's defaults.

**Files:**
- Create: `app/src/main/java/com/ai/tester/config/GlobalExceptionHandler.java`

- [ ] **Step 1: Create the file**

```java
package com.ai.tester.config;

import com.ai.tester.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildErrorResponse(HttpStatus.NOT_FOUND, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI()));
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String path) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(Instant.now().toString());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setPath(path);
        return errorResponse;
    }
}
```

- [ ] **Step 2: Build**

```bash
mvn install -pl app -DskipTests
```

Expected: `BUILD SUCCESS`

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ai/tester/config/GlobalExceptionHandler.java
git commit -m "feat: add GlobalExceptionHandler for 404 and 400 responses"
```

---

## Task 7: Wire VideoGameRequest Through Service and Controller

The service and controller are changed together because they are tightly coupled — changing the service method signatures without updating the controller would break compilation.

**Service changes:**
- `createVideoGame` accepts `VideoGameRequest` instead of `VideoGame`
- `updateVideoGame` accepts `VideoGameRequest`, always stamps path `videoGameId` onto the entity, returns `save()` result directly (removes the redundant second query)
- New private `toEntity(VideoGameRequest)` helper

**Controller changes:**
- `createVideoGame` and `editVideoGame` accept `@Valid @RequestBody VideoGameRequest`
- `createVideoGame`, `deleteVideoGame`, `deleteEvenVideoGames` return `StatusResponse`
- `java.util.Map` import removed

**Files:**
- Modify: `app/src/main/java/com/ai/tester/service/VideoGameService.java`
- Modify: `app/src/main/java/com/ai/tester/controller/VideoGameController.java`

- [ ] **Step 1: Replace VideoGameService**

Full file content:

```java
package com.ai.tester.service;

import com.ai.tester.model.VideoGame;
import com.ai.tester.model.VideoGameList;
import com.ai.tester.model.VideoGameRequest;
import com.ai.tester.repository.VideoGameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoGameService {

    private final VideoGameRepository videoGameRepository;

    public VideoGameService(VideoGameRepository videoGameRepository) {
        this.videoGameRepository = videoGameRepository;
    }

    public VideoGameList getAllVideoGames() {
        return new VideoGameList(videoGameRepository.findAll());
    }

    public VideoGame getVideoGameById(int id) {
        return videoGameRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void createVideoGame(VideoGameRequest request) {
        videoGameRepository.save(toEntity(request));
    }

    @Transactional
    public VideoGame updateVideoGame(VideoGameRequest request, int videoGameId) {
        VideoGame entity = toEntity(request);
        entity.setId(videoGameId);
        return videoGameRepository.save(entity);
    }

    @Transactional
    public void deleteVideoGame(int id) {
        videoGameRepository.deleteById(id);
    }

    @Transactional
    public int deleteEvenGames() {
        return videoGameRepository.deleteEvenGamesLimited();
    }

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
}
```

- [ ] **Step 2: Replace VideoGameController**

Full file content:

```java
package com.ai.tester.controller;

import com.ai.tester.model.StatusResponse;
import com.ai.tester.model.VideoGame;
import com.ai.tester.model.VideoGameList;
import com.ai.tester.model.VideoGameRequest;
import com.ai.tester.service.VideoGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/app/videogames",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
)
@Tag(name = "Video Games")
public class VideoGameController {

    private final VideoGameService videoGameService;

    public VideoGameController(VideoGameService videoGameService) {
        this.videoGameService = videoGameService;
    }

    @GetMapping
    @Operation(summary = "Get all video games", description = "Returns all video games in the database")
    public VideoGameList listVideoGames() {
        return videoGameService.getAllVideoGames();
    }

    @GetMapping("/{videoGameId}")
    @Operation(summary = "Get a video game by ID", description = "Returns a single video game by its ID")
    public VideoGame getVideoGame(
        @Parameter(description = "The video game ID", required = true)
        @PathVariable int videoGameId) {
        return videoGameService.getVideoGameById(videoGameId);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Add a new video game", description = "Adds a new video game to the database")
    public StatusResponse createVideoGame(@Valid @RequestBody VideoGameRequest videoGameRequest) {
        videoGameService.createVideoGame(videoGameRequest);
        return new StatusResponse("Record Added Successfully");
    }

    @PutMapping(
        path = "/{videoGameId}",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @Operation(summary = "Update a video game", description = "Updates an existing video game by ID")
    public VideoGame editVideoGame(
        @Valid @RequestBody VideoGameRequest videoGameRequest,
        @PathVariable int videoGameId) {
        return videoGameService.updateVideoGame(videoGameRequest, videoGameId);
    }

    @DeleteMapping("/{videoGameId}")
    @Operation(summary = "Delete a video game", description = "Deletes a video game from the database by ID")
    public StatusResponse deleteVideoGame(
        @Parameter(description = "The video game ID", required = true)
        @PathVariable int videoGameId) {
        videoGameService.deleteVideoGame(videoGameId);
        return new StatusResponse("Record Deleted Successfully");
    }

    @DeleteMapping("/delete-even-games")
    @Operation(
        summary = "Delete even video game IDs",
        description = "Deletes up to 5 video games with even IDs per request"
    )
    public StatusResponse deleteEvenVideoGames() {
        int deletedCount = videoGameService.deleteEvenGames();
        return new StatusResponse("Deleted " + deletedCount + " records with even IDs");
    }
}
```

- [ ] **Step 3: Build**

```bash
mvn install -pl app -DskipTests
```

Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/ai/tester/service/VideoGameService.java \
        app/src/main/java/com/ai/tester/controller/VideoGameController.java
git commit -m "refactor: wire VideoGameRequest through service and controller"
```

---

## Task 8: Verify with Component Tests

Run the full component test suite against the refactored app. The suite boots the full `App` context — this is the integration verification step.

**Note on known-issue tests:** `getVideoGameByNonExistentIdTest` and `getVideoGameByNonExistentIdXmlErrorResponseTest` are annotated `@KnownIssue` because the app previously returned 500 instead of 404. With `GlobalExceptionHandler` in place, these tests will now pass. The `@KnownIssue` annotations remain on the tests (out of scope), but the test assertions themselves will pass — this is the expected outcome.

- [ ] **Step 1: Run component tests**

```bash
mvn test -pl tests
```

Expected: `BUILD SUCCESS` with all tests passing (or previously-failing known-issue tests now passing).

- [ ] **Step 2: If any test fails unexpectedly**

Check which test fails and what the actual vs expected value is. Common causes:
- XML root element mismatch on error response → verify `@JacksonXmlRootElement(localName = "Map")` on `ErrorResponse`
- 400 returned instead of expected status → check `GlobalExceptionHandler` isn't intercepting a Spring-framework exception it shouldn't
- JSON field missing → verify `VideoGameRequest`/`StatusResponse` field names match what the test expects