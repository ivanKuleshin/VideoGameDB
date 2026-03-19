# XSP-94: [GET /videogames] AC4 – Each returned item contains all required non-null fields

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-94 |
| Status | To Do |
| Priority | Medium |
| Labels | automated, to_automate |

## Description
Verifies that every item in the response contains all six expected fields (id, name, releaseDate, reviewScore, category, rating) and none of them is null. Requirement: XSP-90 – AC4

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-90 | [API] GET /videogames – Retrieve All Video Games | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | Ensure the database contains at least one video game record | | Database has at least one record in the VIDEOGAME table |
| 2 | User should be Authorized | username: test<br>pass: test | Basic auth header is added to the API call |
| 3 | Send GET /app/videogames with Accept: application/json | GET /app/videogames<br>Accept: application/json | A response is received from the server |
| 4 | For every item in the videoGame response array, verify all six fields are present and non-null: id, name, releaseDate, reviewScore, category, rating | | Every item contains id (integer), name (string), releaseDate (ISO-8601 date string), reviewScore (integer), category (string), rating (string) — none is null or missing |

