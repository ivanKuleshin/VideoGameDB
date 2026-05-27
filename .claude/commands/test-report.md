---
description: Phase 5 — Generate the final implementation report from session state
argument-hint: (uses .claude/state/last-*.md)
---

# Phase 5 — Implementation Report

This is an **inline command** — do NOT spawn a subagent.

## Preconditions

1. Read `.claude/state/last-research.md`, `.claude/state/last-plan.md`, `.claude/state/last-implementation.md`, and `.claude/state/last-review.md`. If any is missing, tell the user which phase to re-run and stop.
2. Extract `ticket:` from any of the four files (must agree).

## Steps

1. Compose the report file at:

```
tests/reports/implementation-<TICKET-KEY>-<YYYYMMDD-HHMM>.md
```

2. Use this exact structure:

```markdown
# Implementation Report: <Ticket Key> — <Ticket Summary>

> Generated: <date/time> | Complexity: Simple | Medium | Complex

---

## What Was Implemented

| File | Action |
|------|--------|
| `path/to/TestClass.java` | Created / Updated |

### Test Methods

| Method | @DisplayName | @TmsLink |
|--------|-------------|---------|
| `methodName()` | Human-readable description | XSP-XXX |

**Jira label `automated` added:** Yes / No

---

## Review Summary

| Metric | Value |
|--------|-------|
| Review iterations | <N> / <max> |
| Outcome | Clean / Issues remaining |

### Unresolved Findings
<!-- Omit if Clean -->

| # | Severity | Location | Issue | Skill Reference | Status |
|---|----------|----------|-------|-----------------|--------|
| 1 | High | ClassName.java:L20 | Description | component-testing SKILL.md §X | Unresolved / Rejected-by-user |

### App-Level Blockers
<!-- Omit if none. Source: Info-severity findings from last-review.md. -->

| # | Affected AC | Symptom | Action required |
|---|-------------|---------|-----------------|
| 1 | AC2 | Returns 500 instead of 404 | App fix needed before test can pass |

---

## Assumptions & Notes

<inline notes from `last-implementation.md` and any decisions made during the run>
```

3. Present the path to the user:

> _"Final report saved: `tests/reports/implementation-<KEY>-<timestamp>.md`"_

## Rules

- This command writes the report inline — do not spawn a subagent.
- Source all review data from `last-review.md` — do not rely on conversation context.
- All findings with status `Rejected-by-user` or `Unresolved` go into "Unresolved Findings".
- All `Info` findings from `last-review.md §App-Level Blockers` go into "App-Level Blockers".
