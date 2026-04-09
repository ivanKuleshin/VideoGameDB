---
name: jira-xray-search
description: >-
  Use this skill whenever the user mentions a Jira ticket key alongside any test or automation intent.
  Fetches the ticket from Jira, all linked issues with their types, and every Xray manual test step
  (including called/parent tests), then produces a clean automation-ready overview with full
  acceptance criteria and per-test step tables. Only supports Test and Story ticket types — stops
  with a clear error for Bugs, Epics, or non-existent keys. Trigger on prompts like "fetch context
  for XSP-98", "get test details for PROJ-123", "show me the steps for this ticket", "what does
  XSP-45 test", "I need to automate this story", or any time a ticket key appears in the same
  message as words like test, automate, steps, implement, verify, or context.
disable-model-invocation: true
argument-hint: [JIRA-TICKET-KEY]
allowed-tools: mcp__atlassian__getAccessibleAtlassianResources mcp__atlassian__getJiraIssue mcp__atlassian__searchJiraIssuesUsingJql mcp__atlassian__getJiraIssueRemoteIssueLinks mcp__xray__get_test_case mcp__xray__search_test_cases mcp__xray__get_project_test_cases
---

The ticket key to research is: **$ARGUMENTS**

## Workflow

### 1. Resolve Cloud ID

Call `mcp__atlassian__getAccessibleAtlassianResources`. Use the `id` field from the first result
as `cloudId` for all subsequent Atlassian calls.

### 2. Fetch Main Ticket

Call `mcp__atlassian__getJiraIssue` with the ticket key. Extract:
- Key, summary, status, labels
- Full description text
- Acceptance Criteria — look in the description, custom fields like `customfield_10016`, and any
  "Acceptance Criteria" section in the description body

If the ticket cannot be found, stop and report:
> `❌ Ticket [KEY] not found. Verify the key and try again.`

Check the `issuetype.name` field. This skill only supports `Test` and `Story` issue types.
If the type is anything else (Bug, Epic, Task, Sub-task, etc.), stop and report:
> `❌ Ticket [KEY] is of type [TYPE]. This skill only supports Test and Story tickets.`

### 3. Fetch Linked Issues

Read the `issuelinks` field from the main issue. For each linked issue, call
`mcp__atlassian__getJiraIssue` individually. Stories are the most important — always include
their full description and every AC item, because these define what the automated test must verify.

Extract per linked issue: key, **issue type** (from `issuetype.name` — e.g. `Test`, `Bug`, `Story`),
link type, status, summary, description, AC.

### 4. Fetch Xray Test Steps

The strategy depends on the issue type:

**If the main ticket is an Xray Test** (issue type = `Test`):
- Call `mcp__xray__get_test_case` with the ticket key directly.
- If steps are empty, fall back to `mcp__xray__search_test_cases` with JQL `issue = KEY`.

**If the main ticket is a Story (or any non-Test type)**:
- The Xray test steps live in the linked Test issues already fetched in Step 3.
- For each linked issue of type `Test`, call `mcp__xray__get_test_case` with that issue's key.
- Group steps by test case key in the output.

**Parent test case handling** — after fetching each test case, check if it has a parent test
(a `parent` field in the Jira issue whose type is also `Test`). If so, call
`mcp__xray__get_test_case` on the parent and include its steps under a clearly labelled
**Parent Test** subsection directly above the child's own steps. This matters because Xray
often stores shared precondition steps in a parent test rather than repeating them in every
child test.

Collect all steps: index, action, data, expected result.

### 5. Produce the Output

Structure the output so a test automation engineer can start writing code immediately — no raw
API payloads, no noise. The engineer will decide what to assert based on the expected results
in the test steps; do not add an Automation Notes section.

---

## Automation-Ready Test Case Overview: $ARGUMENTS

### Summary
- **Ticket**: [KEY] — [Summary]
- **Status**: [Status]
- **Labels**: [Labels]

### Acceptance Criteria
- AC1: ...
- AC2: ...

### Linked Issues

#### [LINKED-KEY] — [Summary] | Type: `Story/Test/Bug` | Link: `...` | Status: `...`
- **Description**: ...
- **Acceptance Criteria** _(for Stories)_:
  - AC1: ...
- **Test Steps** _(for Xray Tests)_:

  _If a parent test exists, show its steps first:_

  **Parent Test: [PARENT-KEY] — [Parent Summary]**

  | Step | Action | Data | Expected Result |
  |------|--------|------|-----------------|
  | 1    | ...    | ...  | ...             |

  **This Test: [LINKED-KEY]**

  | Step | Action | Data | Expected Result |
  |------|--------|------|-----------------|
  | 1    | ...    | ...  | ...             |

  _If no parent test, just show the single steps table without subsection headers._

_(repeat for each linked issue; omit empty sections)_