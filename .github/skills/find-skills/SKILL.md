---
name: find-skills
description: Helps users discover, list, and install agent skills. Use this skill whenever the user asks "how do I do X", "find a skill for X", "is there a skill that can...", "what skills do I have", "what's installed", or expresses interest in extending agent capabilities. Also trigger this skill when the user mentions a specialized domain (testing, deployment, design, PR reviews, etc.) and wonders if there's automated help available — even if they don't say the word "skill". When in doubt, use this skill.
---

# Find Skills

This skill helps you discover, list, and install skills from the open agent skills ecosystem.

## What is the Skills CLI?

The Skills CLI (`npx skills`) is the package manager for the open agent skills ecosystem. Skills are modular packages
that extend agent capabilities with specialized knowledge, workflows, and tools.

**Key commands:**

- `npx skills find [query]` - Search for skills interactively or by keyword
- `npx skills add <package>` - Install a skill from GitHub or other sources
- `npx skills check` - Check for skill updates
- `npx skills update` - Update all installed skills

**Browse skills at:** https://skills.sh/

## Listing Installed Skills

When the user asks what skills are currently installed (e.g. "what skills do I have?", "list my skills", "what's installed"):

1. Read `skills-lock.json` in the project root — it contains all installed skills with their sources.
2. Format the list clearly for the user:

```
You have 4 skills installed:

- find-skills       (vercel-labs/skills)
- skill-creator     (anthropics/skills)
- spring-boot-engineer (jeffallan/claude-skills)
- unit-test-wiremock-rest-api (giuseppe-trisciuoglio/developer-kit)

To check for updates: npx skills check
To update all:        npx skills update
```

3. Note the difference between skills in `skills-lock.json` (installed/tracked) and skills present in `.github/skills/` (available in workspace context).

## Finding and Installing Skills

### Step 1: Check What's Already Installed

Before searching, read `skills-lock.json` to see if a relevant skill is already installed. If a suitable skill exists, tell the user rather than suggesting a duplicate installation.

### Step 2: Understand What They Need

Identify:
1. The domain (e.g., React, testing, design, deployment)
2. The specific task (e.g., writing tests, creating animations, reviewing PRs)
3. Whether this is common enough that a skill likely exists

### Step 3: Search for Skills

Run the find command with a relevant query:

```bash
npx skills find [query]
```

For example:

- User asks "how do I make my React app faster?" → `npx skills find react performance`
- User asks "can you help me with PR reviews?" → `npx skills find pr review`
- User asks "I need to create a changelog" → `npx skills find changelog`

The command will return results like:

```
Install with npx skills add <owner/repo@skill>

vercel-labs/agent-skills@vercel-react-best-practices
└ https://skills.sh/vercel-labs/agent-skills/vercel-react-best-practices
```

### Step 4: Present Options to the User

When you find relevant skills, present them with:

1. The skill name and what it does
2. The install command they can run
3. A link to learn more at skills.sh

Example:

```
I found a skill that might help! The "vercel-react-best-practices" skill provides
React and Next.js performance optimization guidelines from Vercel Engineering.

To install it:
npx skills add vercel-labs/agent-skills@vercel-react-best-practices

Learn more: https://skills.sh/vercel-labs/agent-skills/vercel-react-best-practices
```

### Step 5: Offer to Install

If the user wants to proceed, install the skill:

```bash
npx skills add <owner/repo@skill> -g -y
```

The `-g` flag installs globally (user-level) and `-y` skips confirmation prompts.
Always mention these flags so the user understands what's happening.

## Common Skill Categories

When searching, consider these common categories:

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

1. Acknowledge that no existing skill was found
2. Offer to help with the task directly using your general capabilities
3. Suggest the user could create their own skill with `npx skills init`

Example:

```
I searched for skills related to "xyz" but didn't find any matches.
I can still help you with this task directly! Would you like me to proceed?

If this is something you do often, you could create your own skill:
npx skills init my-xyz-skill
```
