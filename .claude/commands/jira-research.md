---
description: Phase 1 — Fetch Jira/Xray context for a ticket and persist a research summary
argument-hint: <jira-ticket-key>
---

# Phase 1 — Jira Research

Argument: `$ARGUMENTS` (expected: a Jira ticket key like `XSP-123`).

If `$ARGUMENTS` is empty, ask the user for the ticket key and stop until they provide one. Do not guess.

## Steps

1. Invoke the `jira-researcher` subagent via the Agent tool. Pass the ticket key as input. The agent knows the state file schema — it is defined in `jira-researcher.md §State File`.
2. Confirm to the user: _"Research complete for [KEY]. Run `/test-plan` next."_

## Rules

- Do not implement code, plan, or review.
- The state file is the contract for `/test-plan` — the `ticket:` front-matter key must be present.
