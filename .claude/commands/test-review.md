---
description: Phase 4 — Review implemented tests, filter Info, optionally apply fixes via Fix Workflow
argument-hint: (uses .claude/state/last-implementation.md)
---

# Phase 4 — Test Review & Fix Loop

## Preconditions

1. Read `.claude/state/last-implementation.md`. If it does not exist, tell the user to run `/test-implement` first and stop.
2. Extract `ticket:`, `complexity:`, and the file list from the `## Files` section.
3. Determine the iteration cap:
   - `Simple` → max 1 iteration
   - `Medium` or `Complex` → max 2 iterations

## Iteration 1

1. Invoke `test-code-reviewer` via the Agent tool. Pass the file list from `last-implementation.md`. Do not pass any prior findings — this is a full review.
2. Receive structured findings.
3. **Filter `Info`-severity findings out before any further action.** Set them aside for `last-review.md`.
4. If the remaining findings list is empty: exit the loop, mark review as Clean, jump to State File.
5. If actionable findings remain, present the CP-2 gate and wait for the user's response:

```
Review iteration <N> — [KEY]

| # | Severity | Location | Issue | Skill Reference | Recommendation |
|---|----------|----------|-------|-----------------|----------------|
| 1 | ...      | ...      | ...   | ...             | ...            |

How would you like to proceed?
- "approve all" — apply all fixes
- "skip 2,4" — dismiss specified items, fix the rest
- "abort" — stop without fixing
```
6. If user approves any findings, invoke `test-automation` with **explicit Fix Workflow instruction**:

   > "Run in Fix Workflow mode. Re-read only the skill sections cited in each finding. Evaluate each finding (decline if it contradicts a skill rule). Apply only accepted fixes. Do NOT re-run the Implementation Workflow. Do NOT add the `automated` Jira label."

   Pass only the approved findings (not the full list, never `Info`-severity).
7. Receive the fix summary (accepted/declined per finding).

## Iteration 2 (Medium / Complex only, if iteration 1 produced fixes)

1. Invoke `test-code-reviewer` in **scoped mode**. Pass the same file list and the iteration-1 findings list. Instruct: _"iteration=2, scoped review — verify only whether the iteration-1 issues were resolved. Do NOT re-review clean areas."_
2. Collect any still-unresolved findings for the state file.

## State File

Write `.claude/state/last-review.md` per the schema in `test-code-reviewer.md §State File`. Include all iterations, all finding decisions, and all app-level blockers.

## Summary

Confirm to the user: _"Review complete for [KEY]. Iterations: <N>/<max>. Outcome: <Clean / Issues remaining: N>. State saved to `.claude/state/last-review.md`. Run `/test-report` next."_

## Rules

- NEVER pass `Info`-severity findings to `test-automation` for fixing. Always filter first.
- Always tell `test-automation` to use Fix Workflow, not Implementation Workflow.
- Track user-dismissed findings — they appear in `last-review.md` as `Rejected-by-user`.
- Cap iterations strictly by `complexity` from `last-implementation.md`.
