---
mode: 'agent'
tools: ['com.atlassian/atlassian-mcp-server/getAccessibleAtlassianResources', 'com.atlassian/atlassian-mcp-server/createJiraIssue', 'xray/add_test_step', 'xray/get_test_case']
description: 'Create Xray Test issues in XSP and populate 4-step test steps from a reviewed plan produced by plan-xray-tests'
---

## Goal

You are acting as a **Senior QA Engineer**. Take the reviewed Xray test plan from the current chat context (produced by `plan-xray-tests`) and implement it in Jira — creating all Test issues and populating all steps via Xray MCP.

## Pre-condition

This prompt is the **second step** of a two-step workflow:
1. `plan-xray-tests` — fetches the story, reads source code, produces the test plan ✅ (already done)
2. **`create-xray-tests`** — takes that plan and implements it in Jira ← you are here

The full test plan (all TC blocks with 4 steps each) must already be present in the chat context. Do **not** re-read source files. Do **not** re-fetch the Jira story. Do **not** modify the plan — implement it exactly as reviewed.

## Phase 1 — Create Test Issues

Resolve `cloudId` via **Atlassian MCP** `getAccessibleAtlassianResources`, then create all Test issues **in parallel** using **Atlassian MCP** `createJiraIssue`:

- `projectKey`: `XSP`
- `issueTypeName`: `Test`
- `summary`: from the plan
- `description`: from the plan

## Phase 2 — Add Test Steps

For every created Test issue, add all 4 steps using **Xray MCP** `add_test_step`.

### Parallelism rules

- **Step 1 to all tests in parallel** → wait for all to complete
- **Step 2 to all tests in parallel** → wait for all to complete
- **Step 3 to all tests in parallel** → wait for all to complete
- **Step 4 to all tests in parallel** → wait for all to complete

Steps within a single test are ordered — never add Step 2 before Step 1 is confirmed.

### Step content

Use the exact Action, Data, and Expected values from the reviewed plan — do not paraphrase or shorten.

## Phase 3 — Verify

Call **Xray MCP** `get_test_case` for **all** created test issues in parallel. Confirm:
- Exactly 4 steps per test
- Step order: Auth → Given → When → Then
- No placeholder values

## Output

After verification, output a summary table:

| Issue | AC | Summary | Steps |
|---|---|---|---|
| [XSP-{key}](https://ivankuleshin.atlassian.net/browse/XSP-{key}) | AC{N} | {summary} | 4 ✅ |

## Rules

1. Do **not** re-read source files or re-fetch the Jira story
2. Do **not** alter any content from the plan
3. If the plan is not present in context, stop and ask the user to run `plan-xray-tests` first
4. All Test issues must be created in parallel
5. Steps across tests are added in parallel per wave; steps within a test are sequential
6. Verify all tests after completion
