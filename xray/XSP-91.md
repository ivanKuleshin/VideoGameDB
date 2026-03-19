# XSP-91: [GET /videogames] AC1 – Successful response returns HTTP 200

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-91 |
| Status | To Do |
| Priority | Medium |
| Labels | automated, to_automate |

## Description
Verifies that the endpoint returns HTTP 200 when the database contains at least one video game record. Requirement: XSP-90 – AC1

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-90 | [API] GET /videogames – Retrieve All Video Games | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | User should be Authorized | username: test<br>pass: test | Basic auth header is added to the API call |
| 2 | Ensure the database contains at least one video game record (precondition is satisfied by the default seed data in schema.sql) | | Database has at least one record in the VIDEOGAME table |
| 3 | Send GET /app/videogames with Accept: application/json | GET /app/videogames<br>Accept: application/json | A response is received from the server |
| 4 | Verify the HTTP response status code | | Response status code is 200 OK |

