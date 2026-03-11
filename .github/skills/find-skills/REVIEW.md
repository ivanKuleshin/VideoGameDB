# Skill Review: `find-skills`

## Overview

A utility skill for discovering and installing agent skills from the open ecosystem via the `npx skills` CLI. At
**143 lines** with no `references/` directory, it is entirely self-contained. This is an externally-sourced skill
(not custom to this project).

---

## Pros

- **Excellent description** — arguably the best description in the repo. It is explicitly pushy (*"When in doubt,
  use this skill"*), lists concrete user phrasings, covers non-obvious trigger contexts (domain mentions without
  saying "skill"), and reads naturally.
- **Clear 5-step workflow** — steps are numbered, sequential, and each has a concrete action with a CLI command
  example.
- **Query → command mapping** — the three inline examples (*"React app faster"* → `npx skills find react
  performance`) immediately show the model how to translate user intent into a search query.
- **Graceful "no results" path** — explicitly covers what to do when no skill is found, preventing silent failure.
- **Category table** — provides 7 domain buckets with example queries, giving the model a starting vocabulary for
  diverse topics.
- **Install flags documented** — `-g -y` flags are explained, so the user understands what running the command
  entails.

---

## Cons

### 1. No project-specific categories
The category table lists generic web, testing, and DevOps domains. Categories specific to this project's stack
(Spring Boot, JAX-RS, Allure, REST Assured, H2, Jersey) are absent, meaning the model won't proactively suggest
skill searches for topics that arise constantly in this codebase.

### 2. Missing guidance on probing the user's need
Step 2 (*"Understand What They Need"*) gives three bullet points to identify, but no guidance on how to ask
follow-up questions when the user's request is ambiguous. A short prompt example would help.

### 3. No discussion of global vs. local install trade-offs
The `-g` flag installs globally (user-level). For a project team, a local install (without `-g`) might be
preferable to keep skills scoped to the project. The skill silently always recommends global without explaining
the choice.

### 4. `skills-lock.json` is the only source of truth for installed skills, but differences with `.github/skills/`
   are mentioned only in one brief sentence. The model is left without guidance on what to do when a skill is in
   `.github/skills/` but not in `skills-lock.json` (e.g., custom skills added manually).

### 5. No mention of `npx skills check` / `npx skills update` in the main workflow
Both commands are listed in the "Key commands" section at the top but are not woven into any of the 5 workflow
steps. If the user asks "are my skills up to date?", the model has to connect the dots itself.

---

## Steps to Improve

1. **Add a project-specific category row** to the table:
   | Category | Example Queries |
   |---|---|
   | This Project | spring boot jersey, allure reporting, rest assured, h2 jdbc, jira xray |

2. **Add a short disambiguation example to Step 2** — e.g.:
   > *"If the user says 'help with tests', clarify: are they asking about writing new tests, fixing failing ones,
   > or understanding the test framework? This shapes the query."*

3. **Clarify the `-g` vs. local install decision**:
   > *"Use `-g` for personal/developer tools; omit it if the project should track the skill in its own
   > `skills-lock.json`."*

4. **Handle the `skills-lock.json` vs. `.github/skills/` discrepancy explicitly** — add a note:
   > *"Skills present in `.github/skills/` but absent from `skills-lock.json` are custom/local skills not
   > tracked by the CLI. Mention this distinction to the user if they ask why a skill isn't listed by
   > `npx skills check`."*

5. **Integrate update check into the listing step** — when listing installed skills, also run
   `npx skills check` and report whether any are outdated, rather than mentioning the command passively.

