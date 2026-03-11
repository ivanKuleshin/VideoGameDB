# Skill Review: `component-testing`

## Overview

A focused guide for implementing Spring Boot component tests inside this project. At **87 lines** it is the most
concise custom skill in the repo. Correctly uses a `references/` directory and points to `code-patterns.md` at the
end.

---

## Pros

- **Concise and scannable** — 87 lines means the model reads the whole thing in one pass with minimal noise.
- **Project-specific rules** — every rule maps directly to a real pattern in this codebase (`AllureSteps`,
  `CommonSteps`, `VideoGameTestDataFixtures`, `@TmsLink`, etc.).
- **Good anti-pattern examples** — rule 6 shows ❌/✅ Allure step descriptions, making the distinction immediately
  clear.
- **Given/When/Then mandate** — rule explicitly requires the structure even for simple cases, enforcing consistency.
- **Progressive disclosure** — detailed code patterns are pushed to `references/code-patterns.md`, keeping the body
  lean.
- **No heavy-handed MUST language** — instructions are written in imperative form with enough context to understand
  intent.

---

## Cons

### 1. Description is too passive and narrow
The description says *"Use this when asked to create, write, or implement…"* — it only triggers when the user
explicitly requests test creation. It misses scenarios like:
- *"Add a test for the delete endpoint"*
- *"Cover the 401 case"*
- *"Make sure this new endpoint has tests"*
No project-specific trigger keywords (JUnit 5, REST Assured, Allure, `@SpringBootTest`) appear in the description.

### 2. "When to Use" context is entirely absent from both description and body
The skill-creator recommends the description include both *what the skill does* and *specific contexts for when to
use it*. The current description only states the action, not the trigger context.

### 3. DDT guidance is superficial
Rule mentions *"If possible, use DDT (Data-Driven Testing)"* but provides no guidance on when it is appropriate,
what annotations to use (`@ParameterizedTest`, `@MethodSource`), or how to structure parameterised fixtures in this
project.

### 4. Missing anti-pattern catalogue
There is no explicit list of what to avoid (e.g., hardcoded IDs outside fixtures, `@Autowired` field injection in
test classes, calling app internals directly). A short "Common Mistakes" section would close the feedback loop
faster.

### 5. No example of a complete test method in the body
The skill references `references/code-patterns.md` for examples but gives no inline illustration of a minimal
end-to-end test. A 10-line annotated snippet would let the model understand the pattern without having to load the
reference file every time.

---

## Steps to Improve

1. **Rewrite the description to be pushy and keyword-rich:**
   > *"Guide for implementing component tests for Spring Boot applications using JUnit 5, REST Assured, Allure, and
   > AssertJ. Trigger this skill whenever the user wants to add, write, fix, or review a test — including phrases
   > like 'cover this endpoint', 'add a test for X', 'make sure this is tested', or 'the test is failing'. Also
   > trigger when the user asks about `@TmsLink`, `AllureSteps`, `VideoGameTestDataFixtures`, or `CommonSteps`."*

2. **Add a `## Quick Example` section** directly in the body — a single 10–15 line annotated test skeleton showing
   `@TmsLink`, `@DisplayName`, `@Log4j2`, Given/When/Then, and `AllureSteps.logStep`. This reduces the chance the
   model skips loading the reference file.

3. **Expand the DDT section** with a short `@ParameterizedTest` + `@MethodSource(VideoGameTestDataFixtures::stream)`
   pattern and an explicit note on when to prefer DDT vs. separate test methods.

4. **Add a `## Common Mistakes` section** (4–5 items) such as:
   - Using `@Autowired` on fields instead of constructor injection
   - Building expected data from hardcoded literals instead of DB queries or fixtures
   - Putting assertions outside `AllureSteps.logStep`
   - Calling `App` internals directly instead of going through `HttpClient`

5. **Ensure `references/code-patterns.md` is explicitly described** in SKILL.md with a short note on what it
   contains (AllureSteps examples, `@TmsLink` patterns, AssertJ cheat-sheet) so the model knows when loading it
   is worth the overhead.

