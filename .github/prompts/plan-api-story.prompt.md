---
agent: 'plan'
description: 'Analyse a Spring Boot JAX-RS endpoint and produce a detailed plan for a Jira Story including all sections and Acceptance Criteria — ready for create-api-story to implement'
---

## Goal

You are acting as a **Technical Business Analyst**. Analyse the provided endpoint source file and supporting files, then
**plan** a complete Jira Story in the format defined below.

Do **not** create anything in Jira — output the full planned story content as Markdown so it can be reviewed and then
passed to `create-api-story` for implementation.

## Inputs

- Endpoint source file: `${file}`
- Security config: [SecurityConfig.java](../../app/src/main/java/com/ai/tester/config/SecurityConfig.java)
- Data model: [VideoGame.java](../../app/src/main/java/com/ai/tester/model/VideoGame.java)
- Database schema: [schema.sql](../../app/src/main/resources/schema.sql)
- Application config: [application.properties](../../app/src/main/resources/application.properties)

## Analysis Checklist

Read all input files and extract:

- [ ] HTTP method and full path (include `/app` prefix from `application.properties`)
- [ ] Path parameters — name, type, Java annotation
- [ ] Auth requirements from `SecurityConfig.java` — which paths are protected, which are public
- [ ] Supported `@Consumes` and `@Produces` content types
- [ ] Exact SQL constant(s) executed — quote verbatim
- [ ] Parameter source used (`BeanPropertySqlParameterSource` vs `MapSqlParameterSource`)
- [ ] Return type and HTTP status code
- [ ] Any missing guards: no 404, duplicate PK, path param vs body id mismatch, etc.
- [ ] Any REST convention deviations: `200` instead of `201`/`204`, no `Location` header, etc.

## Planned Story Format

Output the full story using this exact structure:

### 1. Story Title

`[API] {METHOD} /{path} – {Short human-readable description}`

### 2. Overview

> As a client of the VideoGameDB API, I want to [action], so that [business value].

### 3. Endpoint Details Table

| Property             | Value                                                  |
|----------------------|--------------------------------------------------------|
| **Method**           |                                                        |
| **Path**             |                                                        |
| **Auth required**    |                                                        |
| **Credentials**      | Username: `test` / Password: `test` (if auth required) |
| **Request body**     |                                                        |
| **Path parameters**  |                                                        |
| **Query parameters** |                                                        |

### 4. Authentication

- Derive from `SecurityConfig.java`
- Document: missing credentials → `401`, invalid credentials → `401`
- Note `WWW-Authenticate` header suppression
- List public paths
- Hardcode: **Username: `test` / Password: `test`**, Base64: `dGVzdDp0ZXN0`

### 5. Supported Content Types

| Header         | Values |
|----------------|--------|
| `Content-Type` |        |
| `Accept`       |        |

### 6. Business Logic

Numbered list from source code only:

- SQL executed (verbatim)
- Parameter source
- Return type and status code
- Known bugs / missing guards flagged with ⚠️
- REST deviations

### 7. Request Schema (if request body exists)

| Field | Type | DB Column | Required | Description |
|-------|------|-----------|----------|-------------|

Include JSON and XML request body examples using realistic data.

### 8. Response Schema

- Success HTTP status
- Response shape
- JSON and XML response examples with seed data from `schema.sql`

### 9. Acceptance Criteria

Write each AC in **Given / When / Then** format. Must include:

- ✅ Happy path (JSON)
- ✅ DB persistence check (write endpoints only)
- ✅ XML content type (at least once)
- ✅ Missing `Authorization` header → `401`
- ✅ Invalid credentials (`wrong`/`wrong`) → `401`
- ✅ Non-existent ID → `404` (write correct expected behaviour regardless of current code bugs)
- ✅ Non-integer path param → `404` or `400` (if path param exists)
- ✅ All endpoint-specific edge cases (duplicate PK, body id vs path param, DB defaults, etc.)

> **AC writing rules:**
> - Expected results = correct/desired behaviour only
> - Flag known bugs with `> ⚠️ Known issue` blockquote **below** the AC — never inside the Expected result
> - Hardcode `test`/`test` in every AC that requires auth

### 10. Out of Scope

Bullet list of related endpoints not covered by this story.

## Planning Rules

1. Read **all** input files before producing output
2. Do **not** assume or invent behaviour — derive everything from source code
3. Do **not** create any Jira issues
4. Output the full planned story as Markdown
5. End with a note: _"Review complete. Run `create-api-story` to create this story in Jira."_

