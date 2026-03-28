---
name: find-skills
description: Helps users discover and install agent skills when they ask questions like "how do I do X", "find a skill for X", "is there a skill that can...", or express interest in extending capabilities. Always use this skill when the user mentions wanting to extend agent capabilities, asks if a skill exists for any topic, wonders whether there's a better way to handle a recurring workflow, or says things like "I wish you could help me with X" — even if they don't explicitly use the word "skill".
---

# Find Skills

This skill helps you discover and install skills from the open agent skills ecosystem. Always start by checking
what's already installed locally before searching externally — the answer might already be there.

## What is the Skills CLI?

The Skills CLI (`npx skills`) is the package manager for the open agent skills ecosystem. Skills are modular packages
that extend agent capabilities with specialized knowledge, workflows, and tools.

**Key commands:**

- `npx skills find [query]` - Search for skills interactively or by keyword
- `npx skills add <package>` - Install a skill from GitHub or other sources
- `npx skills check` - Check for skill updates
- `npx skills update` - Update all installed skills

**Browse skills at:** https://skills.sh/

## How to Help Users Find Skills

### Step 1: Check Locally Installed Skills First

Before searching the external ecosystem, check whether an already-installed skill covers the user's need. Run:

```bash
ls .github/skills/
```

Read the `description` field from each relevant skill's `SKILL.md` frontmatter. If an existing skill already handles
the use case, recommend it immediately — no external search needed.

Also check `.github/copilot-instructions.md`: it lists active skills and links to each `AGENTS.md` for quick context.

### Step 2: Understand What They Need

If no local skill covers the request, identify:

1. The domain (e.g., React, testing, deployment, documentation)
2. The specific task (e.g., writing tests, reviewing PRs, generating changelogs)
3. Whether this is a recurring or common enough task that a community skill likely exists

### Step 3: Search for Skills

Run the find command with a focused keyword:

```bash
npx skills find [query]
```

Search examples by intent:

| User Says                                  | Search Query                       |
|--------------------------------------------|------------------------------------|
| "make my React app faster"                 | `npx skills find react performance` |
| "help me with PR reviews"                  | `npx skills find pr review`        |
| "I need to create a changelog"             | `npx skills find changelog`        |
| "automate my deployment pipeline"          | `npx skills find ci-cd deploy`     |
| "help me write better commit messages"     | `npx skills find git commit`       |

If the first query yields nothing useful, try synonyms (e.g., `deploy` → `deployment`, `testing` → `e2e playwright`).

**If `npx` is unavailable** (no Node.js installed), tell the user: "The skills CLI requires Node.js. You can install it
at https://nodejs.org or search for skills at https://skills.sh/." Then offer to help with the task using your general
capabilities in the meantime.

### Step 4: Verify Quality Before Recommending

**Do not recommend a skill based solely on search results.** Always cross-check:

1. **Install count** — Prefer skills with 1K+ installs. Treat anything under 100 with caution.
2. **Source reputation** — Official sources (`vercel-labs`, `anthropics`, `microsoft`, `ComposioHQ`) are more
   trustworthy than unknown authors.
3. **Recency** — Check whether the skill has been updated in the last 6 months; stale skills may lag behind framework
   changes.
4. **Scope match** — Skim the skill's description to confirm it actually addresses what the user needs, not just shares
   keywords.

### Step 5: Present Options to the User

When you find a relevant skill, present it with:

1. The skill name and what it does
2. The install count and source
3. The install command
4. A link to learn more

Example:

```
I found a skill that might help — "react-best-practices" provides React and
Next.js performance optimization guidelines from Vercel Engineering (185K installs).

To install it:
  npx skills add vercel-labs/agent-skills@react-best-practices

Learn more: https://skills.sh/vercel-labs/agent-skills/react-best-practices
```

If multiple good matches exist, list up to three ranked by install count, briefly noting what differentiates each.

### Step 6: Install the Skill

If the user wants to proceed, install it for them. Always clarify scope (project vs. global) before running.

| Option                      | Description                                                                               |
|-----------------------------|-------------------------------------------------------------------------------------------|
| `-g`, `--global`            | Install to the user directory instead of the project (global install).                    |
| `-a`, `--agent <agents...>` | Target specific agents (e.g. `github-copilot`, `claude-code`). Defaults to `github-copilot`. |
| `-s`, `--skill <skills...>` | Install specific skills by name (use `*` to select all).                                  |
| `-l`, `--list`              | List available skills without installing.                                                 |
| `--copy`                    | Copy skill files instead of creating symlinks.                                            |
| `-y`, `--yes`               | Skip all confirmation prompts.                                                            |
| `--all`                     | Install all selected skills to all agents without prompts.                                |

```bash
# Project-level install (default — recommended for team repos)
npx skills add <owner/repo@skill> -y -a github-copilot

# Global install (for personal workflows not tied to a project)
npx skills add <owner/repo@skill> -g -y -a github-copilot
```

After installing, confirm the skill appears under `.github/skills/` and briefly explain how to trigger it.

## Common Skill Categories

| Category        | Example Queries                          |
|-----------------|------------------------------------------|
| Web Development | react, nextjs, typescript, css, tailwind |
| Testing         | testing, jest, playwright, e2e           |
| DevOps          | deploy, docker, kubernetes, ci-cd        |
| Documentation   | docs, readme, changelog, api-docs        |
| Code Quality    | review, lint, refactor, best-practices   |
| Design          | ui, ux, design-system, accessibility     |
| Productivity    | workflow, automation, git                |

## When No Skills Are Found

If no relevant skills exist:

1. Acknowledge that no existing skill was found for the topic.
2. Offer to help with the task directly using your general capabilities.
3. Suggest the user create their own skill with `npx skills init`, or delegate to the `skill-creator` skill if it is
   installed.

Example:

```
I searched for skills related to "xyz" but didn't find any matches.
I can still help you with this task directly — want me to proceed?

If this is something you do often, you could capture it as your own skill:
  npx skills init my-xyz-skill
```
