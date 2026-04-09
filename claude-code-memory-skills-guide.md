# Claude Code: Memory, CLAUDE.md, and Skills — Reference Guide

---

## TL;DR

| | CLAUDE.md | Memory | Skills |
|---|---|---|---|
| **Who writes it** | You | Claude (auto) or You (manual) | You |
| **Loaded** | Every session start | Every session start (first 200 lines) | Descriptions always; full content on invocation |
| **Best for** | Always-on rules and conventions | Learned preferences and corrections | Reference docs and invocable workflows |
| **Version controlled** | Yes | No (machine-local, or yes if manual like this project) | Yes |
| **Triggers workflows** | No | No | Yes (`/skill-name`) |

---

## 1. CLAUDE.md

### What It Is

A plain markdown file you write to give Claude persistent, always-on instructions. Loaded into every session as context.

### Scope Hierarchy (all levels are loaded and concatenated)

| Scope | Path | In git |
|---|---|---|
| Managed policy (org) | `/Library/Application Support/ClaudeCode/CLAUDE.md` | No |
| Personal (all projects) | `~/.claude/CLAUDE.md` | No |
| Project (team-shared) | `./CLAUDE.md` or `./.claude/CLAUDE.md` | Yes |
| Local (personal, this project) | `./CLAUDE.local.md` | No — add to `.gitignore` |

Claude walks up the directory tree and loads every `CLAUDE.md` / `CLAUDE.local.md` it finds, from root down to working directory. **More specific paths take precedence** when instructions conflict.

### What to Put in CLAUDE.md

- Build and test commands (exact commands for your project)
- Architecture overview (module layout, data flow, key components)
- Coding standards (naming conventions, formatting rules, patterns)
- "Never do X" rules (absolute constraints)
- Agent usage instructions (which custom agents exist and when to use them)
- References to other files via `@path/to/file` imports

### Import Syntax

```markdown
See @README for project overview.

# Git Workflow
@docs/git-instructions.md
```

Imports nest up to 5 hops. Relative paths resolve from the file containing the import.

### Splitting Large CLAUDE.md with `.claude/rules/`

```
.claude/
├── CLAUDE.md           # Core instructions — keep under 200 lines
└── rules/
    ├── testing.md      # Loads every session (no paths frontmatter)
    └── api.md          # Loads only for matching files (path-scoped)
```

Path-scoped rules (frontmatter with `paths:` globs) load only when Claude works with matching files — saving context budget.

### Tips

- **Keep under 200 lines.** Beyond that, adherence degrades and context bloat increases.
- Use `<!-- comment -->` HTML comments for maintainer notes — they are stripped before injection.
- If CLAUDE.md grows too large, move reference material to skills.

---

## 2. Memory

Claude Code has two memory approaches: **auto memory** (Claude writes) and **manual memory** (you write — as in this project).

### 2.1 Auto Memory

Claude automatically saves useful learnings across sessions.

**Storage:**
```
~/.claude/projects/<git-repo-path>/memory/
├── MEMORY.md           # Index — loaded at start (first 200 lines / 25KB)
├── topic-a.md          # Detail file — loaded on demand
└── topic-b.md
```

**What loads at session start:** Only `MEMORY.md`. Topic files are read on demand when Claude needs them.

**Enable / disable:**
```json
{ "autoMemoryEnabled": false }
```
Or: `CLAUDE_CODE_DISABLE_AUTO_MEMORY=1`

**Audit memory:** Run `/memory` inside a session.

**Trigger manually:** Say "Remember that..." and Claude saves it to auto memory.

### 2.2 Manual Memory (this project's approach)

This project stores memory inside `.claude/memory/` — version-controlled and team-shareable. CLAUDE.md instructs Claude to read `.claude/memory/MEMORY.md` at the start of every conversation.

**Why:** Auto memory lives at `~/.claude/projects/...` (machine-local, outside the repo). Manual memory inside `.claude/memory/` is committed to git, so the whole team shares it.

### 2.3 Memory Types

| Type | What it captures | Example |
|---|---|---|
| `feedback` | Corrections or confirmed approaches | "Do not flag `@Autowired` in test classes" |
| `user` | Personal workflow preferences | "Always use pnpm, not npm" |
| `project` | Architecture or context not in code | "API tests require a running Redis instance" |
| `reference` | Technical patterns or quirks | Build edge cases, debugging recipes |

### 2.4 Memory File Format

```markdown
---
name: feedback-autowired-tests
description: @Autowired is acceptable in tests/ module; do not suggest field injection removal
type: feedback
---

Do not flag or suggest removing `@Autowired` field injection in the `tests/` module.

**Why:** This is intentional for Spring integration tests in this codebase.
**How to apply:** When reviewing or writing test classes, `@Autowired` is acceptable.
```

`MEMORY.md` is an index with one-line pointers:
```markdown
- [Autowired in Tests](feedback_autowired_tests.md) — @Autowired is acceptable in tests/
- [No Comments/JavaDoc](feedback_no_comments_javadoc.md) — omit inline comments and JavaDoc unless asked
```

### 2.5 What NOT to Save in Memory

- Code patterns, conventions, or architecture — derive from the code itself
- Git history or recent changes — use `git log` / `git blame`
- Debugging solutions or fix recipes — the fix is in the code; the commit has context
- Anything already in CLAUDE.md
- Ephemeral task state (in-progress work, current session context)
- Secrets or credentials

### 2.6 Best Practices

- Keep `MEMORY.md` under 200 lines — it's the only file loaded at startup
- Be specific: "Use 2-space indentation" beats "format nicely"
- Review and prune outdated entries — stale memories consume context every session
- For team-wide rules, prefer CLAUDE.md (version-controlled) over auto memory (machine-local)

---

## 3. Skills

### What Skills Are

Markdown files that extend Claude's capabilities. A skill can be:
- **Reference content** — knowledge Claude applies automatically (API conventions, test patterns)
- **Invocable workflow** — a step-by-step playbook triggered with `/skill-name`
- **Both**

Custom commands have been merged into skills: `.claude/commands/deploy.md` and `.claude/skills/deploy/SKILL.md` both create `/deploy` and behave identically. Skills add extra features (frontmatter control, subagent execution, dynamic injection).

### Where Skills Live

| Scope | Path |
|---|---|
| Personal (all projects) | `~/.claude/skills/<name>/SKILL.md` |
| Project | `.claude/skills/<name>/SKILL.md` |
| Plugin | `<plugin>/skills/<name>/SKILL.md` (namespaced: `plugin:name`) |

Each skill is a **directory** with `SKILL.md` as the entrypoint:
```
.claude/skills/my-skill/
├── SKILL.md            # Required entrypoint
├── reference.md        # Supporting file — loaded on demand
└── examples/
    └── sample.md
```

### How Skills Load

1. **Session start:** Skill *descriptions* (truncated at 250 chars) load into context so Claude knows what's available.
2. **On invocation** (you type `/skill-name` or Claude auto-triggers): Full `SKILL.md` content enters the conversation.
3. Full content stays in context for the remainder of the session.

### SKILL.md Format

```yaml
---
name: my-skill
description: What this does and when to use it. Front-load key use case.
disable-model-invocation: true   # Only you can invoke; Claude cannot auto-trigger
user-invocable: false            # Claude-only; hidden from / menu
allowed-tools: Bash(git add *) Bash(git commit *)
context: fork                    # Run in isolated subagent context
agent: Explore                   # Subagent type when context: fork
model: claude-opus-4-6           # Override model for this skill
paths:
  - "src/api/**/*.ts"            # Auto-activate only for matching files
---

# Skill instructions go here...
```

### Invocation Control

| Frontmatter | You can invoke | Claude auto-triggers | Description in context |
|---|---|---|---|
| (default) | Yes | Yes | Always loaded |
| `disable-model-invocation: true` | Yes | No | Not in context |
| `user-invocable: false` | No | Yes | Always loaded |

Use `disable-model-invocation: true` for skills with side effects (deploy, commit, send message).
Use `user-invocable: false` for background knowledge (internal architecture, legacy context).

### Arguments and Dynamic Injection

```yaml
---
name: fix-issue
description: Fix a GitHub issue by number
disable-model-invocation: true
---

Fix GitHub issue $ARGUMENTS following our coding standards.
```

`/fix-issue 123` → Claude sees "Fix GitHub issue 123 following our coding standards."

**Shell injection** (runs before Claude sees the content):
```yaml
---
name: pr-summary
context: fork
---

## PR context
- Diff: !`gh pr diff`
- Comments: !`gh pr view --comments`

Summarize this pull request...
```

### Creating a Skill

```bash
mkdir -p .claude/skills/my-skill
```

Then write `.claude/skills/my-skill/SKILL.md` with frontmatter + instructions.

---

## 4. Decision Guide

**Use CLAUDE.md when:**
- The rule applies to every session without exception
- It's a project standard the whole team should follow
- It's a build command, architecture overview, or naming convention
- It should be version-controlled and shared via git

**Use memory when:**
- Claude discovered something useful mid-session (build quirk, debugging insight)
- You corrected Claude's behavior and want that correction to persist
- It's a personal preference, not a team standard
- You want automatic persistence without editing files manually

**Use skills when:**
- The content is reference material Claude only needs sometimes (API docs, test conventions)
- It's a repeatable workflow triggered explicitly (`/deploy`, `/commit`, `/review`)
- Instructions are long and would bloat CLAUDE.md if always loaded
- You need isolated execution (`context: fork`)

---

## 5. Context Cost Summary

| Feature | Loaded when | Cost |
|---|---|---|
| CLAUDE.md | Every session start | Always present |
| MEMORY.md | Every session start | Up to 200 lines / 25KB |
| Skill descriptions | Every session start | Small (250 chars per skill) |
| Skill full content | On invocation | Present for rest of session |
| Path-scoped rules | Matching file only | Low |

**Rule of thumb:** If CLAUDE.md exceeds 200 lines, move reference material to skills or split into `.claude/rules/` files. Skills with `disable-model-invocation: true` cost zero context until you invoke them.

---

## 6. Quick Commands

```bash
# Generate a CLAUDE.md from your codebase
/init

# View all loaded memory and rules
/memory

# Create a project skill
mkdir -p .claude/skills/my-skill

# Invoke a skill
/my-skill

# Tell Claude to remember something
"Remember that integration tests require a running Redis instance."

# Add a rule to CLAUDE.md
"Add this to CLAUDE.md: always use the custom DateUtil class."
```