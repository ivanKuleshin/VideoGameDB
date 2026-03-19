# XSP-92: [GET /videogames] AC2 – Response count matches database record count

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-92 |
| Status | To Do |
| Priority | Medium |
| Labels | automated, to_automate |

## Description
Verifies that the number of items in the videoGame array exactly matches the number of records in the database. Requirement: XSP-90 – AC2

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-90 | [API] GET /videogames – Retrieve All Video Games | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | User should be Authorized | username: test<br>pass: test | Basic auth header is added to the API call |
| 2 | Query the database directly and note the total number of records N in the VIDEOGAME table | | The count N is known before the API call |
| 3 | Send GET /app/videogames with Accept: application/json | GET /app/videogames<br>Accept: application/json | A response is received from the server |
| 4 | Verify the size of the videoGame array in the response body equals N | | videoGame array contains exactly N items, matching the database count |

