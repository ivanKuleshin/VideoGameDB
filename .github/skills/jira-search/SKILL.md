---
name: jira-search
description: >-
  Use this skill whenever the user wants to interact with Jira in any way —
  searching for issues, finding bugs or tasks by status/assignee/sprint/label.
  Trigger even when the user doesn't say "Jira" explicitly but is clearly talking about tickets, tasks,
  stories, bugs, sprints, or backlogs. Prefer this skill over ad-hoc tool calls
  so that cloudId resolution and JQL construction are handled consistently.
---

# Jira Search & Interaction

## Precondition

Always use subagent to perform MCP calls. Use output from MCP to provide summarized answer.

## Quick Start: Always Resolve cloudId First

Every Jira MCP tool requires a `cloudId`. Obtain it once at the start of any Jira interaction:

```
getAccessibleAtlassianResources → use the id field of the first result as cloudId
```

Cache this value and reuse it for all subsequent calls in the same session.

---

## Searching for Issues

Use `searchJiraIssuesUsingJql` with a JQL query. Always request only the fields you need to keep responses lean.

### Project-Specific Saved Filters

These saved filters are pre-configured for this project:

| Filter Name       | JQL                            | Purpose                                          |
|-------------------|--------------------------------|--------------------------------------------------|
| Automated tests   | `filter = 'Automated tests'`   | Issues already automated (label: `automated`)    |
| Tests to automate | `filter = 'Tests to automate'` | Issues pending automation (label: `to_automate`) |

### Common JQL Patterns

```
# Issues assigned to current user
assignee = currentUser() AND resolution = Unresolved

# Issues in a specific project by status
project = PROJ AND status = "In Progress"

# Issues updated recently
project = PROJ AND updated >= -7d ORDER BY updated DESC

# Issues by label
project = PROJ AND labels = "regression"

# Sprint issues
project = PROJ AND sprint in openSprints()

# Search by text
project = PROJ AND text ~ "video game" ORDER BY created DESC
```

If a search returns no results, try broadening the query (remove filters one at a time) or confirm the project key with
the user.

---

## Creating & Updating Issues

- **Create**: `createJiraIssue` — requires `projectKey`, `issueTypeName` (Task, Bug, Story), `summary`
- **Update fields**: `editJiraIssue` — pass only the fields that need to change
- **Transition status**: `getTransitionsForJiraIssue` to list valid transitions, then `transitionJiraIssue` with the
  transition `id`
- **Add comment**: `addCommentToJiraIssue` — body accepts Markdown
- **Log work**: `addWorklogToJiraIssue` — time format: `2h`, `30m`, `1d`
- **Link issues**: `jiraRead` with `getIssueLinkTypes` to find valid link type names, then `jiraWrite` with
  `createIssueLink`

---

## Retrieving Issue Details

- **Single issue**: `getJiraIssue` with `issueIdOrKey` (e.g. `PROJ-123`)
- **Full-text search**: `search` (Rovo Search) — use when the user gives a natural language query without CQL/JQL
- **Remote links**: `getJiraIssueRemoteIssueLinks`

---

## Presenting Results

- For search results, summarize in a concise table: key, summary, status, assignee.
- For a single issue, show all relevant fields inline.
- If the user asks for counts or aggregates, compute them from the returned list rather than making additional API
  calls.
