---
name: Project Context
description: VideoGameDB is a two-module Maven project with a Spring Boot app and a black-box component test suite; active known bug XSP-139 blocks 404 tests
type: project
---

VideoGameDB is a two-module Maven project (`app/` + `tests/`). The `tests/` module is a black-box
component test suite using Spring Boot Test, REST Assured, JUnit 5, Allure, and H2.

Known open bug: **XSP-139** — the app returns HTTP 500 instead of 404 for non-existent video game IDs.
Two tests (`getVideoGameByNonExistentIdTest`, `getVideoGameByNonExistentIdXmlErrorResponseTest`) are
`@Disabled` pending the fix. These tests are correctly written; they should be re-enabled once XSP-139 is resolved.

**Why:** Helps future reviews avoid flagging these disabled tests as test defects — they are app-level blockers.
**How to apply:** Classify any 404-related disabled tests as `Info / App-level blocker`, not as test defects.
