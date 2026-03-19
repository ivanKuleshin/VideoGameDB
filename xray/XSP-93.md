# XSP-93: [GET /videogames] AC3 – Returned game names match database records

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-93 |
| Status | To Do |
| Priority | Medium |
| Labels | automated, to_automate |

## Description
Verifies that the names of all returned video games match the names of all records in the database (in any order). Requirement: XSP-90 – AC3

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-90 | [API] GET /videogames – Retrieve All Video Games | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | User should be Authorized | username: test, pass: test | Basic auth header is added to the API call |
| 2 | Query the database directly and collect the names of all records in the VIDEOGAME table | | A list of all game names from the database is available for comparison |
| 3 | Send GET /app/videogames with Accept: application/json | GET /app/videogames, Accept: application/json | A response is received from the server |
| 4 | Extract the name field from every item in the videoGame response array and compare to the list collected from the database | | All names from the API response match all names from the database in any order — no missing or extra entries |

