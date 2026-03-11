# How to Create a Skill

## Why a Skill (not a custom instruction or sub-agent)?

|              | Custom Instruction                             | Sub-agent                   | **Skill**                                             |
|--------------|------------------------------------------------|-----------------------------|-------------------------------------------------------|
| **Scope**    | Always active, affects all sessions            | One-off task delegation     | On-demand, triggered by context                       |
| **Content**  | Behavioral preferences, style rules            | Task execution              | Reusable domain expertise + workflow                  |
| **Reuse**    | Broad, blunt                                   | Not reusable                | Targeted, reusable across sessions                    |
| **Best for** | "Always use AssertJ", "Never wildcard imports" | "Run this migration script" | "Here's how to write component tests in this project" |

**Use a skill when:**

- You have a multi-step workflow you repeat (e.g., "add a new API endpoint + tests")
- Copilot needs domain context that's too big or specific for a custom instruction
- You want the workflow triggered only in relevant situations, not always

---

## Anatomy of a Skill

```
skills/my-skill/
├── SKILL.md          ← required: frontmatter + instructions
├── scripts/          ← optional: reusable scripts Copilot can execute
├── references/       ← optional: large docs loaded on demand
└── assets/           ← optional: templates, icons, etc.
```

**`SKILL.md` minimal structure:**

```markdown
---
name: my-skill
description: What it does AND when to trigger it. Be specific and a bit "pushy" —
             include trigger phrases so Copilot uses it proactively.
---

# My Skill

## When to use this

...

## Steps

...
```

---

## Creating a Skill in 5 Steps

### 1. Define intent (2 min)

Answer three questions:

- What should Copilot do when this skill triggers?
- What user phrases/contexts should trigger it?
- What's the expected output?

### 2. Write `SKILL.md` (10–20 min)

Key rules:

- **`description` field** = primary trigger mechanism. Include *what* it does **and** *when* to use it. Make it slightly
  pushy: *"Use this whenever the user mentions X, even if they don't say 'skill'."*
- **Body** < 500 lines. For large reference material, put it in `references/` and point to it from `SKILL.md`.
- **Explain the *why*** behind instructions — don't just list rules. Copilot reasons better with context.
- **Use imperative form**: *"Read the schema file before generating"*, not *"You should read..."*
- No `MUST`/`NEVER` overuse — prefer explaining reasoning.

### 3. Test it (15 min)

Write 2–3 realistic prompts a real user would type. Run them and review outputs. Ask: *does Copilot follow the workflow?
Is the output correct?*

### 4. Iterate

Edit `SKILL.md` based on what failed. Rerun. Repeat until satisfied.

### 5. Optimize the description (optional but valuable)

The `description` field drives triggering accuracy. If the skill isn't firing when it should (or fires when it
shouldn't), use the `skill-creator` skill to run the description optimization loop — it generates trigger evals and
auto-tunes the description.

---

## Quick Tips

- **Progressive disclosure**: metadata (~100 words) is always in context; `SKILL.md` body loads when triggered;
  `references/` loads only when explicitly needed. Keep each layer lean.
- **Bundle scripts** for repetitive deterministic tasks — if every test run independently writes the same helper script,
  put it in `scripts/` once.
- **One skill per domain** — don't cram unrelated workflows into one skill. Prefer multiple focused skills.
- **Bad description = skill never triggers.** This is the #1 failure mode. Invest time here.

---

## Example: Minimal Skill

```markdown
---
name: add-endpoint
description: Guides adding a new REST endpoint to the app module, including resource
             class, SQL constants, Swagger annotation, and Endpoint enum update.
             Use this whenever the user asks to add, create, or implement a new API
             endpoint or route, even if they just say "add a new GET for X".
---

# Add Endpoint

## Steps

1. Add SQL constant(s) to `VideoGameResource`.
2. Add JAX-RS method with `@GET/@POST/@PUT/@DELETE`, `@Path`, `@Produces`, `@Operation`.
3. Add path to `Endpoint` enum in the `tests` module.
4. If new response shape needed, add model with `@XmlRootElement`.
```

Short, purposeful, immediately actionable.

