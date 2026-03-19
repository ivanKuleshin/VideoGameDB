# XSP-96: [GET /videogames] AC6 – Response is valid XML when Accept: application/xml

## Metadata
| Field | Value |
|-------|-------|
| Key | XSP-96 |
| Status | To Do |
| Priority | Medium |
| Labels | automated, to_automate |

## Description
Verifies that when the Accept: application/xml header is set, the response Content-Type is application/xml and the body is valid XML with a videoGames root element. Requirement: XSP-90 – AC6

## Linked Issues
| Key | Summary | Link Type |
|-----|---------|-----------|
| XSP-90 | [API] GET /videogames – Retrieve All Video Games | tests |

## Test Steps
| Step # | Action | Data | Expected Result |
|--------|--------|------|-----------------|
| 1 | User should be Authorized | username: test<br>pass: test | Basic auth header is added to the API call |
| 2 | Set the request header Accept: application/xml | Accept: application/xml | Request is configured to accept XML response |
| 3 | Send GET /app/videogames | GET /app/videogames<br>Accept: application/xml | A response is received from the server |
| 4 | Verify the response Content-Type header and parse the response body as XML. Check that the root element is videoGames containing videoGame child elements | | Response Content-Type is application/xml; body is valid XML with root element `<videoGames>` containing `<videoGame>` children |

