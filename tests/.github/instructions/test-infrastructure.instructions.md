---
applyTo: "tests/src/main/**"
---

# Test Infrastructure Conventions

## Client Conventions

- `HttpClient` is a singleton (`HttpClient.getInstance()`) initialized via `HttpClientConfig`
- HTTP methods: `get(path, contentType)`, `post(path, body, contentType)`, `put(path, body, contentType)`,
  `delete(path, contentType)`
- `DbClient` interface is implemented by `H2DbClient` using `JdbcTemplate`
- DB queries return `VideoGameDbModel`; `getReleaseDateAsString()` converts epoch millis to date string

## Model Conventions

- JSON response models: `model/api/json/` — use `@Data`, `@JsonProperty` where field name differs
- XML response models: `model/api/xml/` — use `@Data`, `@JacksonXmlRootElement`, `@JacksonXmlProperty`,
  `@JacksonXmlElementWrapper`
- DB models: `model/db/` — use `@Data`, `@JsonProperty` matching DB column names (uppercase)
- Shared canonical model for comparisons is `VideoGameApiModel` — both JSON and XML responses are mapped to it

## Builder Conventions

- Test data builders live in `builder/` package
- Use fluent `with*()` methods and a terminal `build()` returning `Map<String, Object>`
- Builders must provide sensible defaults for all fields so tests only override what they need
- ⚠️ **`VideoGameBuilder` is discouraged** — do not use it to construct test data for insert operations.
  Use `VideoGameTestDataFixtures` instead.

## Endpoint Conventions

- API endpoints are defined as an enum in `data/Endpoint` with a `@Getter path` field
- Always reference endpoints via the enum constant (e.g. `VIDEOGAMES.getPath()`)

## Utility Conventions

- Utility classes are `final` with a private constructor (or `@NoArgsConstructor(access = PRIVATE)`)
- `XmlUtil.parse(String, Class<T>)` — parses XML strings using a shared `XmlMapper`
- `DateUtil.epochMillisToDateString(long)` — converts epoch millis to `LocalDate.toString()`

## Properties Conventions

- Test properties in `application-test.properties` support env-var overrides (e.g. `${BASE_URL:http://localhost}`)

