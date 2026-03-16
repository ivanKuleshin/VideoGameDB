---
description: 'Create a Jira Story in XSP from a reviewed plan produced by plan-api-story'
---

## Goal

You are acting as a **Technical Business Analyst**. Take the reviewed story plan from the current chat context (produced by `plan-api-story`) and create the Jira Story in the **XSP** project using the Atlassian MCP tool.

## Pre-condition

This prompt is the **second step** of a two-step workflow:
1. `plan-api-story` — analyses the code and produces the story plan ✅ (already done)
2. **`create-api-story`** — takes that plan and creates it in Jira ← you are here

The full story content (title, all sections, ACs) must already be present in the chat context from the previous step. Do **not** re-analyse the source code. Do **not** modify or rewrite the plan — use it as-is.

## Implementation Steps

1. Resolve `cloudId` via **Atlassian MCP** `getAccessibleAtlassianResources`
2. Call **Atlassian MCP** `createJiraIssue` with:
   - `projectKey`: `XSP`
   - `issueTypeName`: `Story`
   - `summary`: the title from the plan (format: `[API] {METHOD} /{path} – {description}`)
   - `description`: the full Markdown story body from the plan, containing all sections verbatim
3. Output the created issue key and direct URL: `https://ivankuleshin.atlassian.net/browse/{key}`

## Rules

1. Do **not** re-read or re-analyse source files
2. Do **not** alter any content from the plan — titles, ACs, examples, ⚠️ notes must be preserved exactly
3. If the plan is not present in context, stop and ask the user to run `plan-api-story` first
