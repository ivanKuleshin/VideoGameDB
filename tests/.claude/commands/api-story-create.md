---
description: Create a Jira Story in XSP from a reviewed plan produced by /api-story-plan
---

## Goal

You are acting as a **Technical Business Analyst**. Take the reviewed story plan from the current chat context (produced by `/api-story-plan`) and create the Jira Story in the **XSP** project using the Atlassian MCP tools.

## Pre-condition

This command is the **second step** of a two-step workflow:
1. `/api-story-plan` — analyses the code and produces the story plan (already done)
2. **`/api-story-create`** — takes that plan and creates it in Jira (you are here)

The full story content (title, all sections, ACs) must already be present in the chat context from the previous step. Do **not** re-analyse the source code. Do **not** modify or rewrite the plan — use it as-is.

## Implementation Steps

1. Resolve `cloudId` via `mcp__com_atlassian_atlassian-mcp-server__getAccessibleAtlassianResources`
2. Call `mcp__com_atlassian_atlassian-mcp-server__createJiraIssue` with:
   - `projectKey`: `XSP`
   - `issueTypeName`: `Story`
   - `summary`: the title from the plan (format: `[API] {METHOD} /{path} – {description}`)
   - `description`: the full Markdown story body from the plan, containing all sections verbatim
3. Output the created issue key and direct URL: `https://ivankuleshin.atlassian.net/browse/{key}`

## Rules

1. Do **not** re-read or re-analyse source files
2. Do **not** alter any content from the plan — titles, ACs, examples, warning notes must be preserved exactly
3. If the plan is not present in context, stop and ask the user to run `/api-story-plan` first
