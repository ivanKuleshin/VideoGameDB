# Skill Review: `jira-search`

## Overview

A concise, project-aware Jira interaction skill. At **94 lines** with no `references/` directory, it is entirely
self-contained. Covers issue search, CRUD operations, and result presentation.

---

## Pros

- **Best-in-class description** — matches `find-skills` for pushiness. Explicitly triggers on implicit contexts
  (tickets, tasks, stories, backlogs), covers both technical (*"JQL construction"*) and non-technical usage, and
  includes the key phrase *"Prefer this skill over ad-hoc tool calls"*.
- **Cloud ID resolution is the first thing** — the *"Always Resolve cloudId First"* quick-start section prevents
  the most common failure mode immediately.
- **Project-specific saved filters** — the `Automated tests` / `Tests to automate` filter table is directly useful
  for this project and eliminates repeated JQL construction.
- **JQL pattern catalogue** — six ready-to-use patterns cover the majority of daily Jira searches without the model
  needing to construct JQL from scratch.
- **Concise and scannable** — at 94 lines the entire skill is read in one pass, with zero filler.
- **Fallback broadening hint** — *"If a search returns no results, try broadening the query…"* prevents silent
  dead-ends.

---

## Cons

### 1. References a non-existent tool (`addWorklogToJiraIssue`)
The *"Creating & Updating Issues"* section lists:
> *"Log work: `addWorklogToJiraIssue` — time format: `2h`, `30m`, `1d`"*

This tool does not exist in the available MCP tool set. Referencing it will cause the model to attempt a
non-existent tool call and fail.

### 2. References a non-existent tool (`jiraWrite` / `createIssueLink`)
> *"Link issues: `jiraRead` with `getIssueLinkTypes` to find valid link type names, then `jiraWrite` with
> `createIssueLink`"*

`jiraWrite` does not exist as a tool. The available tool is `mcp_com_atlassian_jiraRead` (for read operations only).
The link-creation path is currently broken.

### 3. "Presenting Results" section is underspecified
The section says *"summarize in a concise table: key, summary, status, assignee"* but gives no example table and
no guidance on what to do when a field is missing (e.g., unassigned issue). A one-row example would eliminate
ambiguity.

### 4. No error-handling guidance
There is no guidance on what to do if `getAccessibleAtlassianResources` fails, returns no resources, or if a JQL
query throws a 400. Users hitting Jira auth errors or misconfigured projects have no recovery path in the skill.

### 5. No distinction between `search` (Rovo) and `searchJiraIssuesUsingJql`
Both tools exist but their use cases overlap. The skill mentions using `search` for *"natural language queries
without CQL/JQL"* in one line, but doesn't explain the trade-offs (Rovo searches across Jira + Confluence; JQL
is Jira-only but precise). New users won't know which to reach for.

### 6. Project filters will silently become stale
The saved filters (`Automated tests`, `Tests to automate`) are hardcoded JQL strings. If the filter name or JQL
changes in Jira, the skill gives no indication that validation against the actual Jira filter definition is needed.

---

## Steps to Improve

1. **Remove or replace the `addWorklogToJiraIssue` entry** — either delete the line or replace it with the correct
   tool call pattern once identified. Add a comment that worklog support depends on available MCP tools.

2. **Fix the `jiraWrite` / `createIssueLink` entry** — either remove it until the tool is available, or describe
   an alternative workaround (e.g., adding a comment with a manual link reference).

3. **Add a one-row example to "Presenting Results"**:
   ```
   | Key      | Summary                        | Status      | Assignee      |
   |----------|--------------------------------|-------------|---------------|
   | XSP-42   | Add DELETE /videogames test    | In Progress | Ivan Kuleshin |
   ```

4. **Add a short error-handling section** — at minimum:
   - If `getAccessibleAtlassianResources` returns empty → tell the user to check Atlassian connection.
   - If JQL returns 400 → simplify the query and try again.

5. **Add a one-paragraph "Rovo Search vs. JQL" decision guide** — something like:
   > *"Use `search` (Rovo) for natural language and when searching across Jira + Confluence. Use
   > `searchJiraIssuesUsingJql` when you need precise filtering by field, label, status, or sprint."*

6. **Add a validation note for project-specific filters**:
   > *"These filters are correct as of March 2026. If results are unexpected, verify the filter definition in Jira
   > under Filters > View all filters."*

