---
description: 'Read an existing XSP Story and plan one Xray Test issue per AC with detailed steps — ready for create-xray-tests to implement'
---

## Goal

You are acting as a **Senior QA Engineer**. Given a Jira Story key, fetch the story, read the source code, and **plan**
one Xray manual Test issue per Acceptance Criteria — complete with test steps in full detail.

Do **not** create anything in Jira. Output the full plan as a structured document so it can be reviewed, then passed to
`create-xray-tests` for implementation.

## Input

Story issue key: `${input:storyKey:e.g. XSP-115}`

## Pre-flight: Read Context Files

Before planning anything, read these files:

- Source code: [VideoGameResource.java](../../app/src/main/java/com/ai/tester/resource/VideoGameResource.java)
- Security config: [SecurityConfig.java](../../../app/src/main/java/com/ai/tester/config/SecurityConfig.java)
- Database schema: [schema.sql](../../../app/src/main/resources/schema.sql)
- Data model: [VideoGame.java](../../../app/src/main/java/com/ai/tester/model/VideoGame.java)

Then fetch the Story from Jira via **Atlassian MCP** `getJiraIssue` and parse all Acceptance Criteria.

## Plan Output Format

For each AC, output a test case block in this exact format:

---
**TC{N} — AC{N}: {AC title}**

- **Summary:** `[{METHOD} /{path}] AC{N} – {short scenario}`
- **Description:** One sentence + `Requirement: {storyKey} – AC{N}`

| Step | Action | Data | Expected |
|------|--------|------|----------|
| 1    | ...    | ...  | ...      |
| 2    | ...    | ...  | ...      |
| 3    | ...    | ...  | ...      |
| 4    | ...    | ...  | ...      |

---

## Step Structure Rules

### Step 1 — Auth

Pick exactly one variant:

**Variant A — Valid credentials**

- Action: `User should be Authorized`
- Data: `username: test, password: test`
- Expected: `Basic Auth header is added to the API call: Authorization: Basic dGVzdDp0ZXN0`

**Variant B — No credentials**

- Action: `Ensure no Authorization header is set in the request`
- Data: `No Authorization header included`
- Expected: `Request has no auth credentials — Authorization header is absent`

**Variant C — Invalid credentials**

- Action: `Set invalid Basic Auth credentials in the request`
- Data: `username: wrong, password: wrong` / `Authorization: Basic d3Jvbmc6d3Jvbmc=`
- Expected: `Request has incorrect Basic Auth credentials — Authorization header is present but invalid`

### Step 2 — Given (precondition / data setup)

- **Write/update/delete tests**: always `INSERT` fresh isolated data — never rely on seed data
- Use unique IDs per test starting from `101` incrementing per AC
- Include both INSERT and confirming SELECT SQL in the Data field
- **Read tests**: `SELECT` to confirm seed data exists
- **Non-existent ID tests**: `SELECT * FROM VIDEOGAME WHERE ID = 99999` → 0 rows
- **Duplicate PK tests**: `SELECT` to confirm seed record ID `1` already exists
- **Body id ≠ path param tests**: insert two records with distinct IDs (e.g. `104` and `105`)
- Expected: confirm DB state before the request

### Step 3 — When (send the API request)

Action format: `Send {METHOD} {path} {with/without [body type]}`

Data field — full request block:

```
{METHOD} {path}
Authorization: Basic {token}
Content-Type: {type}
Accept: {type}

Body:
{exact JSON or XML}
```

Rules:

- Omit Body section for requests without a body (GET, DELETE)
- Omit or replace Authorization line for auth failure tests
- Use concrete values only — no placeholders
- JSON: use realistic game data (real titles, release dates, scores)
- XML: encode `<` as `&lt;` and `>` as `&gt;`
- Expected: `A response is received from the server`

### Step 4 — Then (assertion)

Action: describe what is verified (status code, response body, headers, DB state)

**Expected result = correct/desired behavior only — never document bugs here**

Use these patterns:

| AC type                | Expected result                                                                                  |
|------------------------|--------------------------------------------------------------------------------------------------|
| Happy path JSON        | `Status 200 OK. Response body: {exact JSON}`                                                     |
| Happy path XML         | `Status 200 OK. Content-Type: application/xml. Response body: {exact XML}`                       |
| DB persistence         | `{N} row(s) returned: {all field values matching request body}`                                  |
| Missing credentials    | `Status 401 Unauthorized. Response body contains no game data`                                   |
| Invalid credentials    | `Status 401 Unauthorized. Response body contains no game data`                                   |
| Auth + DB unchanged    | `Status 401 Unauthorized. DB row is unchanged: {original field values}`                          |
| Non-existent ID        | `Status 404 Not Found`                                                                           |
| Non-integer path param | `Status 404 or 400 — JAX-RS cannot bind "{value}" to int @PathParam`                             |
| Duplicate PK           | `Status 500 Internal Server Error`                                                               |
| DB defaults            | `Status 200 OK. DB row: {field=value for all columns}`                                           |
| Body id ≠ path param   | Both IDs verified: path param record updated, body id record unchanged                           |
| Success string body    | `Status 200 OK. Response body: {"status": "Record Added Successfully"}`                          |
| Delete confirmed       | `Status 200 OK. Response body: {"status": "Record Deleted Successfully"}. SELECT returns 0 rows` |

For write operations, always include a DB verification SQL in the Data field of Step 4.

## Credential Reference

| Purpose          | Value              |
|------------------|--------------------|
| Valid (plain)    | `test` / `test`    |
| Valid (Base64)   | `dGVzdDp0ZXN0`     |
| Invalid (plain)  | `wrong` / `wrong`  |
| Invalid (Base64) | `d3Jvbmc6d3Jvbmc=` |

## Planning Rules

1. Read all context files and fetch the Jira Story before producing output
2. Derive all SQL, field names, and response shapes from source code — do not assume
3. Do **not** create any Jira issues or call any write MCP tools
4. Each AC gets exactly one test case with several steps
5. Use isolated test data IDs starting from `101` for write/update/delete tests
6. Expected results = correct behavior only; note known bugs separately below the table if needed
7. End with: _"Plan complete. Review the test cases above, then run `create-xray-tests` to implement."_
8. Provide a detailed TC by steps output to the user, they should be able to review the TCs

