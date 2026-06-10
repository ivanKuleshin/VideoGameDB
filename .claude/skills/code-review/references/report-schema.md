# Review State File Schema (`last-review.md`)

Canonical schema for the review state file written at the end of Phase 4
(`/test-review`) to `.claude/state/last-review.md`.

Every review run MUST produce this file using the exact section order below.

## Required Sections

| Section                 | Purpose                                                                         |
|-------------------------|---------------------------------------------------------------------------------|
| Front-matter            | Machine-readable metadata (ticket, complexity, iterations, outcome, timestamp)  |
| `## Summary`            | Jira number + a quick prose description of what was reviewed and the net result |
| `## Highlights`         | Scannable bullets calling out what was fixed, skipped-by-user, and deferred     |
| `## Iteration <N>`      | Full findings table per iteration, with a `Decision` column                     |
| `## App-Level Blockers` | App-side issues outside test-suite scope (omit if none)                         |
| `## Files Modified`     | Files touched by the Fix Workflow, with a one-line note each                    |

## Field Rules

- **Front-matter** — `ticket`, `complexity` (`Simple`/`Medium`/`Complex`), `iterations`
  (actual run), `max_iterations` (cap from complexity), `outcome`
  (`Clean` / `Fixed` / `Issues remaining: N`), `generated` (ISO-8601 UTC).
- **`## Summary`** — first line is the linked Jira key (`[XSP-NNN](url)`) plus title;
  then one short paragraph describing the change set and the review result across all iterations.
- **`## Highlights`** — use status icons so the outcome is scannable at a glance:
    - `✅ Fixed:` — finding accepted and applied
    - `⏭️ Skipped by user:` — actionable finding the user dismissed (`Rejected-by-user`)
    - `⛔ Deferred (app blocker):` — valid issue outside the test suite's control
- **Iteration tables** — one row per finding. Include EVERY finding from that iteration,
  including `Info`-severity ones (mark them `Filtered (Info) — not actioned`).
  Columns: `# | Severity | Location | Issue | Skill Reference | Decision`.
- **`Decision` values** (the only allowed set):
    - `Accepted — fixed`
    - `Rejected-by-user`
    - `Filtered (Info) — not actioned`
    - `Resolved — confirmed` (iteration 2 scoped verification)
    - `Unresolved` (still open after the final iteration)
- Each iteration section ends with a short **`### Fix Outcome`** line summarizing
  accepted/declined counts and compilation status.

## Template

```markdown
---
ticket: XSP-160
complexity: Medium
iterations: 2
max_iterations: 2
outcome: Issues remaining: 1
generated: 2026-06-10T14:32:00Z
---

## Summary

**Jira:** [XSP-160](https://jira/browse/XSP-160) — Add PUT /videogames/{id} update validation tests

**Changes reviewed:** Component tests for the update-video-game endpoint covering
positive update, invalid-payload rejection, and non-existent-ID handling. Review
spanned 2 iterations: 4 findings raised in iteration 1 (3 fixed, 1 skipped by user),
1 follow-up finding confirmed resolved in iteration 2, 1 app-level blocker deferred.

## Highlights

- ✅ **Fixed:** DB cleanup moved inside `try/finally` so inserted rows (IDs 101–103) are always removed.
- ✅ **Fixed:** Added DB-state assertion after PUT to verify the row was actually updated, not just the HTTP 200.
- ✅ **Fixed:** Replaced magic numbers with named constants in `UpdateVideoGameBaseTest`.
- ⏭️ **Skipped by user:** Suggested parameterizing the invalid-payload cases via `@MethodSource`
  (Finding #4) — user chose to keep explicit `@Test` methods for readability.
- ⛔ **Deferred (app blocker):** Endpoint returns 500 instead of 404 for unknown IDs — tracked as XSP-161,
  outside test-suite scope.

## Iteration 1

### Findings

| # | Severity | Location | Issue | Skill Reference | Decision |
|---|----------|----------|-------|-----------------|----------|
| 1 | Major | `UpdateVideoGameComponentTest.java:L78-L80` | Setup insert ran outside `try` block — `finally` cleanup skipped on mid-test failure | db-testing SKILL.md §Database Cleanup Strategy | Accepted — fixed |
| 2 | Major | `UpdateVideoGameComponentTest.java:L82-L110` | No DB-state assertion after PUT — only HTTP status checked, update not verified | component-testing SKILL.md §Main Rules rule 5 | Accepted — fixed |
| 3 | Minor | `UpdateVideoGameBaseTest.java:L40` | Magic number `200` instead of `HttpStatus.OK.value()` | component-testing SKILL.md §Main Rules rule 2 | Accepted — fixed |
| 4 | Minor | `UpdateVideoGameComponentTest.java:L120-L155` | Three near-identical invalid-payload tests could use `@MethodSource` | component-testing SKILL.md §Test Data | Rejected-by-user |
| 5 | Info | `UpdateVideoGameComponentTest.java:L15` | Unused import `java.util.List` | — | Filtered (Info) — not actioned |

### Fix Outcome

3 of 4 actionable findings accepted and applied. Finding #4 dismissed by user.
Compilation passed (BUILD SUCCESS).

## Iteration 2 (scoped)

### Findings

| # | Severity | Location | Issue | Skill Reference | Decision |
|---|----------|----------|-------|-----------------|----------|
| 1 | Major | `UpdateVideoGameComponentTest.java:L84-L112` | Verify iteration-1 finding #2 resolved — DB assertion now present and correct | component-testing SKILL.md §Main Rules rule 5 | Resolved — confirmed |

### Fix Outcome

No new fixes required. Iteration-1 fixes verified intact.

## App-Level Blockers

| Blocker | Location | Impact | Tracking |
|---------|----------|--------|----------|
| PUT returns 500 (not 404) for unknown ID | `VideoGameController` / `VideoGameService` | Cannot assert 404 path | XSP-161 (app fix) |

## Files Modified

- `tests/src/test/java/com/ai/tester/updateVideoGame/UpdateVideoGameBaseTest.java` — added `OK_STATUS`/`UPDATED_NAME`
  constants
- `tests/src/test/java/com/ai/tester/updateVideoGame/UpdateVideoGameComponentTest.java` — findings #1, #2, #3 applied
```

## Notes

- For a `Simple` ticket (max 1 iteration), include only `## Iteration 1`; omit
  `## App-Level Blockers` when there are none.
- A `Clean` review (no actionable findings) still records the iteration table with
  any `Info`-severity findings and an `outcome: Clean` front-matter value.

