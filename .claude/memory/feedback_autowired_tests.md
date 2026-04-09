---
name: @Autowired acceptable in test module
description: @Autowired field injection is acceptable in the tests/ module — do not flag or replace it
type: feedback
---

`@Autowired` field injection is acceptable in the `tests/` module.

**Why:** User explicitly confirmed this is fine for the test context.

**How to apply:** Do not suggest replacing `@Autowired` with constructor injection in test classes. Do not flag it as a code smell or violation during reviews of the `tests/` module.