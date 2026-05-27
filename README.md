# VideoGameDB

## Test Automation with Claude Code

The Jira-driven test-automation workflow runs as a sequence of five manual slash commands. Each command writes a state file under `.claude/state/` that the next command reads — the pipeline is decoupled, so steps can run in separate sessions.

| # | Command | Description | Input | Output |
|---|---------|-------------|-------|--------|
| 1 | `/jira-research XSP-123` | Fetches ticket details, acceptance criteria, and Xray test steps from Jira | Jira ticket key | structured Jira/Xray summary -> `.claude/state/last-research.md` |
| 2 | `/test-plan [XSP-123]` | Analyses research and produces a test plan with complexity rating and codebase file pointers | last research, **or** ticket key in standalone mode | plan with `complexity:` field, scenarios, file targets -> `.claude/state/last-plan.md` |
| 3 | `/test-implement` | Writes test code from the plan, verifies compilation, and labels the Jira ticket | last plan | created/modified test files; compilation verified; `automated` label added on Jira -> `.claude/state/last-implementation.md` |
| 4 | `/test-review` | Reviews implemented tests for quality; user selects findings to fix | last implementation | review findings + applied fixes (user approves which findings to fix) |
| 5 | `/test-report` | Generates a markdown implementation report summarising the full pipeline run | session state files | `tests/reports/implementation-XSP-123-<timestamp>.md` |

### Jira/Xray Ticket Creation

Five additional commands live under `tests/.claude/commands/` (run them from the `tests/` directory):

- `/api-story-plan <endpoint-source-path>` — analyse a JAX-RS endpoint and produce a Story plan
- `/api-story-create` — create the Story in Jira from the planned content in chat context
- `/xray-tests-plan <story-key>` — read a Story and plan one Xray manual Test per AC with detailed steps
- `/xray-tests-create` — create all planned Test issues and populate steps via Xray MCP
- `/xray-test-single <story-key> <test-method>` — create a single Xray Test for an existing test method

### What if...

- **Skip a step:** commands are independent; you can run any command if the prior `.claude/state/last-*.md` file exists
- **Re-run a step:** re-running overwrites the corresponding `.claude/state/last-*.md` file
- **Abort:** close the session — state files persist for next time

State files are gitignored (`.claude/state/`), so each developer keeps their own pipeline state.

## Tech Stack

| Component              | Version       |
|------------------------|---------------|
| Java                   | 21            |
| Spring Boot            | 3.5.3         |
| Jersey (JAX-RS)        | Jakarta EE 10 |
| H2 Database            | 2.3.232       |
| swagger-jaxrs2-jakarta | 2.2.28        |
| Swagger UI (webjar)    | 5.18.2        |

## Requirements

- **Java 21** or higher
- **Maven 3.9+**

## Running the Application

```bash
mvn spring-boot:run
```

## Endpoints

Once running, the application is available at:

| URL                                   | Description                       |
|---------------------------------------|-----------------------------------|
| http://localhost:8080/swagger-ui.html | Swagger UI (interactive API docs) |
| http://localhost:8080/h2-console      | H2 in-memory database console     |
