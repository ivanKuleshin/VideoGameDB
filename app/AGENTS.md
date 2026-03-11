# app/AGENTS.md

## Module Overview

Spring Boot 3.5.3 REST API for managing video games. Jersey (JAX-RS) handles all business endpoints under `/app/**`;
Spring MVC handles static assets (`/swagger-ui.html`, `/webjars/**`, `/h2-console`).

## Source Layout

```
app/src/main/java/com/ai/tester/
  app/
    App.java                 ← @SpringBootApplication entry point
    AppResourceConfig.java   ← Registers JAX-RS resources, Jackson + JAXB providers, Swagger
    WebConfig.java           ← Spring MVC config (Swagger UI static routing)
  config/
    SecurityConfig.java      ← HTTP Basic Auth (in-memory user: test/test)
  model/
    VideoGame.java           ← Entity; @XmlRootElement, @XmlJavaTypeAdapter for LocalDate
    VideoGameList.java       ← List wrapper; @XmlRootElement(name="videoGames")
    LocalDateAdapter.java    ← JAXB adapter converting LocalDate ↔ String
  resource/
    VideoGameResource.java   ← All endpoints; uses NamedParameterJdbcTemplate directly
app/src/main/resources/
  schema.sql                 ← H2 DDL + 10 seed rows (IDs 1–10); runs on every context start
  application.properties     ← Jersey as filter, basePath=/app, H2 config
```

## Endpoint Reference

All endpoints are under `/app/videogames` and produce/consume both `application/json` and `application/xml`.

| Method | Path                              | Returns            | Notes                                      |
|--------|-----------------------------------|--------------------|--------------------------------------------|
| GET    | `/videogames`                     | `VideoGameList`    |                                            |
| GET    | `/videogames/{videoGameId}`       | `VideoGame`        | Returns first row; throws if not found     |
| POST   | `/videogames`                     | JSON string        | `{"status": "Record Added Successfully"}`  |
| PUT    | `/videogames/{videoGameId}`       | `VideoGame`        | Returns updated row                        |
| DELETE | `/videogames/{videoGameId}`       | JSON string        | `{"status": "Record Deleted Successfully"}`|
| DELETE | `/videogames/delete-even-games`   | JSON object        | Deletes up to 5 rows with even IDs at once |

## Key Design Decisions

- **Jersey as a Servlet Filter** (`spring.jersey.type=filter`) so `/swagger-ui.html` and `/h2-console` are served by
  Spring MVC — Jersey only intercepts `/app/**`.
- **No repository layer** — `VideoGameResource` injects `NamedParameterJdbcTemplate` directly; SQL is defined as
  private constants in the resource class.
- **Dual serialization**: JSON via Jackson (`JavaTimeModule`, `WRITE_DATES_AS_TIMESTAMPS=false`), XML via JAXB
  (`@XmlRootElement`). Both providers are registered in `AppResourceConfig`.
- **`releaseDate` column** is named `released_on` in the DB schema but maps to `releaseDate` in `VideoGame` via
  `VideoGameMapper.mapRow()`.

## Database Schema

Table: `VIDEOGAME(ID, NAME, RELEASED_ON DATE, REVIEW_SCORE INT, CATEGORY, RATING)`.  
Seeded with IDs 1–10 on every application start (`spring.sql.init.mode=always`).

## Security

HTTP Basic Auth only. Single in-memory user: `test` / `test`. Public paths: `/swagger-ui.html`, `/webjars/**`,
`/h2-console/**`, `/app/openapi.json`. All `/app/**` endpoints require authentication.

## Adding a New Endpoint

1. Add SQL constant(s) to `VideoGameResource`.
2. Add a JAX-RS method with `@GET`/`@POST`/`@PUT`/`@DELETE`, `@Path`, and `@Operation` (Swagger).
3. If a new response shape is needed, add a model class with `@XmlRootElement` (for XML support).
4. Add the path to `Endpoint` enum in the `tests` module.

## Build

```bash
mvn install -pl app -DskipTests   # build and install to local repo
mvn spring-boot:run -pl app       # run locally on port 8080
```

