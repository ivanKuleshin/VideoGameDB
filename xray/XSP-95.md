# XSP-95: [GET /videogames] AC5 – Response is valid JSON when Accept: application/json

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-95 |
| Status | To Do |
| Priority | Medium |
| Labels | automated, to_automate |

## Description
Verifies that when the Accept: application/json header is set, the response Content-Type is application/json and the body is valid JSON containing a videoGame array. Requirement: XSP-90 – AC5

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-90 | [API] GET /videogames – Retrieve All Video Games | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | User should be Authorized | username: test<br>pass: test | Basic auth header is added to the API call |
| 2 | Set the request header Accept: application/json | Accept: application/json | Request is configured to accept JSON response |
| 3 | Send GET /app/videogames | GET /app/videogames<br>Accept: application/json | A response is received from the server |
| 4 | Verify the response Content-Type header and parse the response body as JSON. Check that the body contains a videoGame array | | Response Content-Type is application/json; body is valid JSON with structure `{ "videoGame": [ ... ] }` |

