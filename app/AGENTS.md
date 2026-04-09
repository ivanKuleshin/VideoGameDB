# app/AGENTS.md

## Module Overview

Spring Boot 3.5.3 REST API for managing video games. Spring MVC handles business endpoints under `/app/**` and
infrastructure paths (`/swagger-ui.html`, `/v3/api-docs/**`, `/h2-console`).

## Source Layout

```
app/src/main/java/com/ai/tester/
  app/
    App.java                 ← @SpringBootApplication entry point
  config/
    OpenApiConfig.java       ← OpenAPI metadata + HTTP Basic security scheme
    SecurityConfig.java      ← HTTP Basic Auth (in-memory user: test/test)
  model/
    VideoGame.java           ← Entity; @XmlRootElement, @XmlJavaTypeAdapter for LocalDate
    VideoGameList.java       ← List wrapper; @XmlRootElement(name="videoGames")
    LocalDateAdapter.java    ← JAXB adapter converting LocalDate ↔ String
  controller/
    VideoGameController.java ← REST endpoints under /app/videogames
  service/
    VideoGameService.java    ← Business operations used by controller
  repository/
    VideoGameRepository.java ← Spring Data JPA repository + native delete-even query
app/src/main/resources/
  schema.sql                 ← H2 DDL + 10 seed rows (IDs 1–10); runs on every context start
  application.properties     ← H2/JPA config + springdoc OpenAPI paths
```

## Endpoint Reference

All endpoints are under `/app/videogames` and produce/consume both `application/json` and `application/xml`.

| Method | Path                            | Returns         | Notes                                                     |
|--------|---------------------------------|-----------------|-----------------------------------------------------------|
| GET    | `/videogames`                   | `VideoGameList` |                                                           |
| GET    | `/videogames/{videoGameId}`     | `VideoGame`     | Throws if not found                                       |
| POST   | `/videogames`                   | JSON string     | `{"status": "Record Added Successfully"}`                 |
| PUT    | `/videogames/{videoGameId}`     | `VideoGame`     | Returns updated row                                       |
| DELETE | `/videogames/{videoGameId}`     | JSON string     | `{"status": "Record Deleted Successfully"}`               |
| DELETE | `/videogames/delete-even-games` | JSON object     | Deletes up to 5 rows with even IDs; returns deleted count |

## Key Design Decisions

- **Layered MVC architecture** — `VideoGameController` delegates to `VideoGameService`, which uses
  `VideoGameRepository` (`JpaRepository<VideoGame, Integer>`).
- **Delete-even behavior is DB-driven** — `VideoGameRepository.deleteEvenGamesLimited()` uses a native SQL query to
  delete up to 5 even IDs in one request.
- **Dual serialization**: endpoints produce/consume JSON and XML (`MediaType.APPLICATION_JSON_VALUE`,
  `MediaType.APPLICATION_XML_VALUE`) with model-level XML annotations.
- **`releaseDate` column** is named `released_on` in the DB schema and maps to `releaseDate` via
  `@Column(name = "released_on")` in `VideoGame`.
- **OpenAPI is configured through springdoc** — `OpenApiConfig` defines API metadata and the HTTP Basic security
  scheme used by Swagger UI.

## Database Schema

Table: `VIDEOGAME(ID, NAME, RELEASED_ON DATE, REVIEW_SCORE INT, CATEGORY, RATING)`.  
Seeded with IDs 1–10 on every application start (`spring.sql.init.mode=always`).

## Security

HTTP Basic Auth only. Single in-memory user: `test` / `test`. Public paths include `/swagger-ui.html`,
`/swagger-ui/**`, `/v3/api-docs/**`, and `/h2-console/**`. All business API endpoints under `/app/**` require
authentication.

## Adding a New Endpoint

1. Add a Spring MVC handler to `VideoGameController` with `@GetMapping`/`@PostMapping`/`@PutMapping`/`@DeleteMapping`
   and `@Operation` metadata.
2. Implement the business logic in `VideoGameService`.
3. Add or extend data access in `VideoGameRepository` (derived query methods or `@Query` where needed).
4. If a new response shape is needed, add/update model classes with XML annotations so JSON and XML stay aligned.
5. Add the path to `Endpoint` enum in the `tests` module.

## Build

```bash
mvn install -pl app -DskipTests   # build and install to local repo
mvn spring-boot:run -pl app       # run locally on port 8080
```

