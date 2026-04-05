# Implementation Report: XSP-115 — [API] PUT /videogames/{videoGameId} – Update an Existing Video Game

> Generated: 2026-04-05 | Complexity: Complex

---

## What Was Implemented

| File | Action |
|------|--------|
| `tests/src/main/java/com/ai/tester/model/api/json/UpdateVideoGameRequestModel.java` | Created |
| `tests/src/main/java/com/ai/tester/model/api/xml/UpdateVideoGameXmlRequestModel.java` | Created |
| `tests/src/main/java/com/ai/tester/actions/api/put/UpdateVideoGameActions.java` | Created |
| `tests/src/main/java/com/ai/tester/actions/api/put/UpdateVideoGameApiActions.java` | Created |
| `tests/src/test/java/com/ai/tester/putVideoGame/UpdateVideoGameBaseTest.java` | Created |
| `tests/src/test/java/com/ai/tester/putVideoGame/UpdateVideoGameComponentTest.java` | Created |
| `tests/src/main/java/com/ai/tester/data/fixtures/VideoGameTestDataFixtures.java` | Updated |

### Test Methods

| Method | @DisplayName | @TmsLink |
|--------|-------------|---------|
| `updateVideoGameJsonPositiveTest()` | Update video game with valid JSON request | XSP-116, XSP-117 |
| `updateVideoGameXmlPositiveTest()` | Update video game with valid XML request | XSP-118 |
| `updateVideoGamePathParamDrivesUpdateTest()` | Update video game when path parameter ID differs from request body ID | XSP-119 |
| `updateVideoGameMissingCredentialsReturns401Test()` | Update video game without authentication credentials | XSP-120 |
| `updateVideoGameInvalidCredentialsReturns401Test()` | Update video game with invalid authentication credentials | XSP-121 |
| `updateVideoGameNonExistentIdReturns404Test()` | Update non-existent video game | XSP-122 |
| `updateVideoGameNonIntegerIdReturns404Or400Test()` | Update video game with non-integer path parameter | XSP-123 |

**Jira label `automated` added:** ❌ No — Atlassian API not reachable from CI session; add manually to XSP-115

---

## Review Summary

| Metric | Value |
|--------|-------|
| Review iterations | 2 / 2 |
| Outcome | ✅ Clean |

### App-Level Blockers

| # | Affected AC | Symptom | Action required |
|---|-------------|---------|-----------------|
| 1 | AC4 (XSP-119) | PUT updates the record matching body `id` instead of path param `videoGameId` | App fix needed in VideoGameService/VideoGameController before test can be enabled |
| 2 | AC7 (XSP-122) | PUT returns HTTP 500 instead of 404 when `videoGameId` does not exist | App fix needed (add existence check + throw 404 in VideoGameService) before test can be enabled |

---

## Assumptions & Notes

- XSP-116 (HTTP 200 response) and XSP-117 (DB persistence) are combined into a single `@TmsLinks`-annotated test because both acceptance criteria are satisfied by the same PUT operation end-to-end.
- `PUT_JSON_INITIAL` (ID 112) and `PUT_JSON_UPDATED` (ID 112) share the same fixture ID by design: the initial row is inserted, updated via PUT, then cleaned up in a `finally` block. Same pattern applies to `PUT_XML_INITIAL` / `PUT_XML_UPDATED` (ID 113).
- Fixtures `PUT_PATH_PRIMARY` (ID 114) and `PUT_PATH_SECONDARY` (ID 115) use distinct IDs to allow the path-param vs. body-ID conflict scenario (XSP-119) to be verified unambiguously.
- `NON_EXISTING_GAME_ID = 99999` is used for the non-existent game test (XSP-122); absence in DB is pre-verified via `commonSteps.verifyGameNotExistsInDatabase()` before the PUT call.
- `NON_INTEGER_GAME_ID = "abc"` is used for the non-integer path parameter test (XSP-123); the assertion accepts either 400 or 404 because Spring MVC may return either depending on routing resolution.
- The `UpdateVideoGameActions` interface + `UpdateVideoGameApiActions` implementation follow the Actions pattern established in the POST test suite, keeping HTTP dispatch logic out of test classes.
- `UpdateVideoGameXmlRequestModel` serializes via `XmlMapper` in `prepareSerializedXmlBody()` because REST Assured cannot directly serialize Jackson XML-annotated models as a body — the raw XML string is passed instead.

