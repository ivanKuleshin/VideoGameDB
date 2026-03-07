---
description: 'Create a single Xray Test in Jira for an existing test method, mapped to an AC from a given story'
---

## Goal

You are acting as a **Senior QA Engineer**. Given a test method name and a Jira story key, create a single Xray Test
issue in Jira, populate its manual steps, link it to the story, and annotate the source code with `@TmsLink`.

## Inputs

| Parameter       | Description                               | Example                            |
|-----------------|-------------------------------------------|------------------------------------|
| `{STORY_KEY}`   | Jira story the test covers                | `XSP-133`                          |
| `{TEST_METHOD}` | Exact test method name in the source code | `deleteEvenVideoGamesPositiveTest` |

## Phase 1 — Gather Context

Run **in parallel**:

1. Fetch the story via **Atlassian MCP** `getJiraIssue` — extract all ACs from the description
2. Fetch the example test **XSP-131** via **Xray MCP** `get_test_case` — use as format reference
3. Read the test source file containing `{TEST_METHOD}` — understand Given/When/Then logic

Use `getAccessibleAtlassianResources` to resolve `cloudId` if not already known.

## Phase 2 — Map to AC(s)

Analyse `{TEST_METHOD}` and select the AC(s) from `{STORY_KEY}` that it covers:

- Match based on what the test asserts (response status, body content, DB state, auth behaviour, etc.)
- A single test may cover multiple ACs if they are naturally exercised together (e.g., AC-1 + AC-2)
- Prefer the most specific match — avoid mapping to ACs the test does not verify

## Phase 3 — Create Test Issue

Call **Atlassian MCP** `createJiraIssue`:

- `projectKey`: `XSP`
- `issueTypeName`: `Test`
- `summary`: `[{endpoint path}] AC{N} – {concise scenario description}`
- `description`: one sentence describing what is verified; end with `Requirements: {STORY_KEY} – AC{N}`
- `labels`: `["to_automate"]`
- `priority`: `Medium`

## Phase 4 — Add Manual Steps

Add steps using **Xray MCP** `add_test_step` in this fixed order — wait for each step to complete before adding the
next:

| # | Step purpose                                              | Maps to      |
|---|-----------------------------------------------------------|--------------|
| 1 | Auth precondition — confirm valid credentials are used    | Given (auth) |
| 2 | DB / state precondition — confirm initial data state      | Given (data) |
| 3 | Send the HTTP request                                     | When         |
| 4 | Verify response status code                               | Then         |
| 5 | Verify response body content                              | Then         |
| 6 | Verify DB / side-effects (if the test checks persistence) | Then         |

Omit step 6 if `{TEST_METHOD}` makes no DB assertions. Use concrete values from the story (endpoints, status codes, JSON
examples) in the `data` and `result` fields — no placeholder text.

## Phase 5 — Link to Story

1. Call **Atlassian MCP** `jiraWrite` with `action: createIssueLink`, type `Test`: new test issue **tests**
   `{STORY_KEY}`
2. Add `@TmsLink("{new_issue_key}")` annotation to `{TEST_METHOD}` in the source file
    - Import `io.qameta.allure.TmsLink` if not already present
    - Place `@TmsLink` directly above `@Test`

## Phase 6 — Verify

Call **Xray MCP** `get_test_case` for the created issue. Confirm:

- Correct number of steps
- No placeholder values in any step
- Linked to `{STORY_KEY}`

## Output

After verification, output a one-line summary:

> ✅ **{new_issue_key}** — [{summary}](https://ivankuleshin.atlassian.net/browse/{new_issue_key}) | covers `{STORY_KEY}`
> AC{N} | linked & annotated

## Rules

1. Never use `mcp_xray_create_test_case` — always use `createJiraIssue` with `issueTypeName: Test`
2. Steps are sequential within the test — never add the next step before the previous is confirmed
3. Preserve exact endpoint paths, status codes, and JSON examples from the story — no paraphrasing
4. If the test method is not found in the codebase, stop and ask the user to verify the name
5. If no AC matches the test behaviour, list candidate ACs and ask the user to confirm before proceeding

