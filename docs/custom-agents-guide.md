# Custom Agent File Guidelines

Instructions for creating effective and maintainable custom agent files that provide specialized expertise for specific
development tasks in GitHub Copilot.

## Project Context

- Target audience: Developers creating custom agents for GitHub Copilot
- File format: Markdown with YAML frontmatter
- File naming convention: lowercase with hyphens (e.g., `test-specialist.agent.md`)
- Location: `.github/agents/` directory (repository-level) or `agents/` directory (organization/enterprise-level)
- Purpose: Define specialized agents with tailored expertise, tools, and instructions for specific tasks
- Official documentation: https://docs.github.com/en/copilot/how-tos/use-copilot-agents/coding-agent/create-custom-agents

## Required Frontmatter

Every agent file must include YAML frontmatter with the following fields:

```yaml
---
description: 'Brief description of the agent purpose and capabilities'
name: 'Agent Display Name'
tools: [ 'read', 'edit', 'search' ]
model: 'Claude Sonnet 4.5'
target: 'vscode'
---
```

### Core Frontmatter Properties

- **description** (REQUIRED): Single-quoted string, clearly stating the agent's purpose and domain expertise (50-150 characters)
- **name** (OPTIONAL): Display name for the agent in the UI; defaults to filename if omitted
- **tools** (OPTIONAL): List of tool names/aliases; if omitted, agent has access to all available tools
- **model** (STRONGLY RECOMMENDED): Specifies which AI model the agent should use (e.g. `'Claude Sonnet 4.5'`, `'gpt-4o'`)
- **target** (OPTIONAL): Target environment — `'vscode'` or `'github-copilot'`; if omitted, available in both
- **user-invocable** (OPTIONAL): Boolean; default `true`; set `false` to hide from picker while allowing subagent invocation
- **disable-model-invocation** (OPTIONAL): Boolean; default `false`; set `true` to prevent subagent invocation
- **metadata** (OPTIONAL, GitHub.com only): Name-value pairs for agent annotation
- **mcp-servers** (OPTIONAL, Organization/Enterprise only): Configure MCP servers available only to this agent
- **handoffs** (OPTIONAL, VS Code only): Enable guided sequential workflows between agents

## Handoffs Configuration

Handoffs enable guided sequential workflows transitioning between agents. Defined in frontmatter:

```yaml
handoffs:
  - label: Start Implementation
    agent: implementation
    prompt: 'Now implement the plan outlined above.'
    send: false
```

| Property | Required | Description |
|----------|----------|-------------|
| `label`  | Yes      | Display text on the handoff button |
| `agent`  | Yes      | Target agent identifier |
| `prompt` | No       | Pre-filled prompt text for the target agent |
| `send`   | No       | If `true`, auto-submits the prompt (default: `false`) |

Common patterns: Planning → Implementation → Review, Write Failing Tests → Write Passing Code.

## Tool Configuration

| Alias     | Category         | Description                                 |
|-----------|------------------|---------------------------------------------|
| `execute` | Shell execution  | Execute commands in appropriate shell       |
| `read`    | File reading     | Read file contents                          |
| `edit`    | File editing     | Edit and modify files                       |
| `search`  | Code search      | Search for files or text in files           |
| `agent`   | Agent invocation | Invoke other custom agents                  |
| `web`     | Web access       | Fetch web content and search                |
| `todo`    | Task management  | Create and manage task lists (VS Code only) |

Best practices: Principle of Least Privilege — only enable tools necessary for the agent's purpose.

## Sub-Agent Invocation (Agent Orchestration)

Enable agent invocation by including `agent` in the orchestrator's `tools` list. Use a consistent wrapper prompt:

```text
This phase must be performed as the agent "<AGENT_NAME>" defined in "<AGENT_SPEC_PATH>".

IMPORTANT:
- Read and apply the entire .agent.md spec (tools, constraints, quality standards).
- Work on "<WORK_UNIT_NAME>" with base path: "<BASE_PATH>".
- Perform the necessary reads/writes under this base path.
- Return a clear summary (actions taken + files produced/modified + issues).
```

Key points:
- Pass variables in prompts using `${variableName}` for all dynamic values
- Run steps sequentially when dependencies exist between outputs/inputs
- Sub-agents cannot access tools that aren't available to their parent orchestrator
- Avoid orchestration for large-scale data processing (hundreds of files, more than 5-10 sequential steps)

## Agent Prompt Structure

1. **Agent Identity and Role**: Who the agent is and its primary role
2. **Core Responsibilities**: What specific tasks the agent performs
3. **Approach and Methodology**: How the agent works
4. **Guidelines and Constraints**: What to do/avoid and quality standards
5. **Output Expectations**: Expected output format and quality

## Variable Definition and Extraction

Document expected parameters in a `## Dynamic Parameters` section. Extraction methods:
1. **Explicit User Input**: Ask user if not detected in prompt
2. **Implicit Extraction**: Automatically parse from natural language input
3. **Contextual Resolution**: Derive from file context, workspace, or settings

Variable best practices: clear documentation, consistent naming (`projectName`, `basePath`, `outputDir`), validation constraints.

## File Organization and Naming

- Repository-level agents: `.github/agents/` — available only in the specific repository
- Organization/Enterprise-level agents: `agents/` root — available across all repositories
- Naming convention: lowercase with hyphens (e.g. `test-specialist.agent.md`)
- Allowed characters: `.`, `-`, `_`, `a-z`, `A-Z`, `0-9`

## Agent Creation Checklist

**Frontmatter**: `description` present and descriptive, single-quoted; `tools` configured; `model` specified.

**Prompt Content**: Clear agent identity, core responsibilities, approach, guidelines, output expectations; total under 30,000 characters.

**File Structure**: Lowercase-with-hyphens filename; correct directory; `.agent.md` extension.

## Common Agent Patterns

- **Testing Specialist**: All tools; analyze, identify gaps, write tests, avoid production code changes
- **Implementation Planner**: `['read', 'search', 'edit']`; create documentation, avoid implementation
- **Code Reviewer**: `['read', 'search']`; analyze, suggest improvements, no direct modifications
- **Refactoring Specialist**: `['read', 'search', 'edit']`; analyze patterns, implement safely
- **Security Auditor**: `['read', 'search', 'web']`; scan code, check against OWASP, report findings

## Version Compatibility

- **GitHub.com**: Does not support `model`, `argument-hint`, `handoffs` properties
- **VS Code / JetBrains / Eclipse / Xcode**: Supports `model`, `argument-hint`, `handoffs`; cannot configure MCP servers at repository level

## Additional Resources

- [Creating Custom Agents](https://docs.github.com/en/copilot/how-tos/use-copilot-agents/coding-agent/create-custom-agents)
- [Custom Agents Configuration](https://docs.github.com/en/copilot/reference/custom-agents-configuration)
- [Custom Agents in VS Code](https://code.visualstudio.com/docs/copilot/customization/custom-agents)
- [Awesome Copilot Agents Collection](https://github.com/github/awesome-copilot/tree/main/agents)

