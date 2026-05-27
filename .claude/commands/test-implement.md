---
description: Phase 3 — Implement test scenarios from the last plan; CP-3 gate for Complex tickets
argument-hint: (uses .claude/state/last-plan.md)
---

# Phase 3 — Test Implement

## Preconditions

1. Read `.claude/state/last-plan.md`. If it does not exist, tell the user to run `/test-plan` first and stop.
2. Parse the front-matter and extract `ticket:` and `complexity:`.

## CP-3 Gate (Complex only)

If `complexity: Complex`, present the gate before proceeding:

```
This is a Complex ticket — review before implementation begins.

Ticket: <KEY>
Scenarios in plan: <count>
Target files: <list>

Reply "proceed" to start implementation, or "abort" to stop.
```

Wait for "proceed" or "abort". If `complexity` is `Simple` or `Medium`, skip and proceed automatically.

## Steps

1. Invoke the `test-automation` subagent via the Agent tool in **Implementation Workflow** mode. Pass the full content of `last-plan.md` as input. Instruct it explicitly to:
   - Read `.claude/skills/component-testing/SKILL.md` and `.claude/skills/db-testing/SKILL.md` first
   - Read every file listed in the plan's `### Codebase Pointers`
   - Implement all scenarios
   - Validate compilation (use `mcp__ide__getDiagnostics` if available; otherwise `mvn compile -pl tests -q`)
   - Add the `automated` Jira label
2. Write `.claude/state/last-implementation.md` from the returned summary per the schema in `test-automation.md §State File`.
3. Confirm to the user: _"Implementation complete for [KEY]. Run `/test-review` next."_

## Rules

- Do not review code in this command — `/test-review` owns review.
- Re-running overwrites `.claude/state/last-implementation.md`.
