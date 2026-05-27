---
description: Phase 2 — Build a test-implementation plan with complexity and codebase pointers
argument-hint: [jira-ticket-key for standalone mode or <empty> to use last research]
---

# Phase 2 — Test Plan

## Input resolution

1. If `.claude/state/last-research.md` exists, read it and use its `ticket:` value plus body as the input for the planner.
2. If `.claude/state/last-research.md` does not exist:
   - If `$ARGUMENTS` is a ticket key (e.g., `XSP-123`), invoke `test-planner` in **standalone mode** with that key — the planner will self-fetch Jira/Xray data.
   - If neither is available, ask the user to either run `/jira-research <KEY>` first or to provide a ticket key now. Stop.

## Steps

1. Invoke the `test-planner` subagent via the Agent tool with the resolved input.
2. Write the returned plan to `.claude/state/last-plan.md` per the schema in `test-planner.md §State File`.
3. Present the full plan content to the user, then confirm: _"Plan saved to `.claude/state/last-plan.md`. Run `/test-implement` next."_

## Rules

- Do not implement code or modify source.
- Re-running this command overwrites `.claude/state/last-plan.md`.
