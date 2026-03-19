# XSP-97: [GET /videogames] AC7 – Empty database returns 200 with empty list

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-97 |
| Status | To Do |
| Priority | Medium |
| Labels | to_automate |

## Description
Verifies that when the database contains no video game records, the endpoint returns HTTP 200 with an empty videoGame array instead of an error response. Requirement: XSP-90 – AC7

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-90 | [API] GET /videogames – Retrieve All Video Games | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | Delete all records from the VIDEOGAME table so the database is empty | | VIDEOGAME table contains zero records |
| 2 | User should be Authorized | username: test<br>password: test | Basic auth header is added to the API call |
| 3 | Send GET /app/videogames with Accept: application/json | GET /app/videogames<br>Accept: application/json | A response is received from the server |
| 4 | Verify the HTTP response status code and the videoGame array in the response body | | Response status code is 200 OK and the videoGame array is empty ([]) — no error status or error body is returned |

