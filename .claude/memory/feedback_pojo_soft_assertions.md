---
name: Prefer soft assertions over usingRecursiveComparison for POJO field verification
description: User prefers soft assertions per field over usingRecursiveComparison when verifying POJO response models
type: feedback
---

Do not use `usingRecursiveComparison().ignoringFields(...)` for POJO assertions in tests.

**Why:** Field-by-field soft assertions are easier to maintain — if a field name changes, the failure message points directly to the affected field. `usingRecursiveComparison` hides structural mismatches and makes refactoring harder.

**How to apply:** When asserting a response POJO (e.g. `ErrorResponseXmlModel`), use `SoftAssertions` and assert each verifiable field in its own `assertThat(...)` call inside a single `AllureSteps.logStep`. Dynamic/non-deterministic fields (e.g. `timestamp`) get their own separate `logStep` with `isNotBlank()` or similar. Do not use `usingRecursiveComparison()`.