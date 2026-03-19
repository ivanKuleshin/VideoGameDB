# XSP-99: [GET /videogames/{id}] AC1 – Successful response returns HTTP 200

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-99 |
| Status | To Do |
| Priority | Medium |
| Labels | automated, to_automate |

## Description
Verifies that the endpoint returns HTTP 200 when valid credentials and an existing video game ID are provided. Requirement: XSP-98 – AC1

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-98 | [API] GET /videogames/{videoGameId} – Retrieve a Single Video Game by ID | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | User should be Authorized | username: test, password: test | Basic Auth header is added to the API call: `Authorization: Basic dGVzdDp0ZXN0` |
| 2 | Confirm video game with ID 1 exists in the database | `SELECT * FROM VIDEOGAME WHERE ID = 1` | 1 row returned: id=1, name=Resident Evil 4, released_on=2005-10-01, review_score=85, category=Shooter, rating=Universal |
| 3 | Send GET /app/videogames/1 | `GET /app/videogames/1`<br>`Authorization: Basic dGVzdDp0ZXN0`<br>`Accept: application/json` | A response is received from the server |
| 4 | Verify the HTTP response status code | | Response status code is 200 OK |

