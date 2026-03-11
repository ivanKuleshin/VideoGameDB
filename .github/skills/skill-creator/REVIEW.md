# Skill Review: `skill-creator`

## Overview

The meta-skill for creating, iterating, and optimising other skills. At **486 lines** it is the longest skill in
the repo and sits just 14 lines under its own stated 500-line limit. Has the richest bundled-resource structure:
`agents/` (grader, comparator, analyzer), `references/` (schemas), `scripts/` (8 Python scripts), and `assets/`
(eval reviewer HTML).

---

## Pros

- **Comprehensive lifecycle coverage** — captures the full skill development loop: intent → draft → test → evaluate
  → iterate → describe → package. Nothing is glossed over.
- **Progressive disclosure is genuinely used** — the body delegates to `agents/grader.md`, `agents/comparator.md`,
  `agents/analyzer.md`, `references/schemas.md`, `scripts/aggregate_benchmark.py`,
  `eval-viewer/generate_review.py`, and `assets/eval_review.html`. Each is referenced with an explicit *"when to
  read/use"* signal. This is the gold standard for progressive disclosure in the skills repo.
- **Good explanation of the "why"** — critical guidance is reasoned rather than just mandated. For example, the
  section on generalising from feedback explains *why* overfitting test cases defeats the purpose of a skill,
  not just *what* to avoid.
- **Environment-aware branching** — separate sections for Claude Code, Claude.ai, and Cowork environments
  cover the real constraints of each (subagents, display availability, CLI access), preventing silent failures.
- **Theory of mind framing** — the "Improving the skill" section actively coaches the model to understand *why*
  the user wrote what they wrote, not just to act on literal instructions. This is unusually thoughtful.
- **Bundled scripts eliminate repeated work** — `aggregate_benchmark.py`, `run_loop.py`, and `package_skill.py`
  mean the model doesn't re-invent report aggregation or description optimisation on every run.
- **Description is clear and functional** — covers the four main entry points (create, edit, optimise, run evals)
  with sufficient specificity to trigger correctly.

---

## Cons

### 1. Violates its own 500-line rule
The skill explicitly states: *"Keep SKILL.md under 500 lines; if you're approaching this limit, add an additional
layer of hierarchy along with clear pointers about where the model should go next."* At **486 lines**, the body is
14 lines under the limit but clearly past the "approaching" threshold. The "Description Optimization" section
alone (Steps 1–4, ≈ 80 lines) and the "Claude.ai-specific instructions" + "Cowork-Specific Instructions" sections
(≈ 60 lines combined) are strong candidates for extraction to a `references/` file.

### 2. Self-contradictory use of all-caps
The skill coaches: *"if you find yourself writing ALWAYS or NEVER in all caps … that's a yellow flag"*. Yet the
body contains:
- `"GENERATE THE EVAL VIEWER *BEFORE* evaluating inputs yourself"` (repeated twice)
- `"Do NOT use /skill-test or any other testing skill."`

These read exactly like the heavy-handed instructions the skill advises against. The repetition suggests the
instruction is a patch for model non-compliance rather than structural guidance — which points to an underlying
design issue worth solving differently.

### 3. Description is not "pushy" enough by its own standard
The skill says descriptions should be *"a little bit 'pushy'"* and should *"not undertrigger"*. The current
description lists four use cases but doesn't include casual trigger phrases like *"I want to improve this skill"*,
*"my skill isn't triggering"*, *"can you make this skill better"*, or *"turn this conversation into a skill"*.

### 4. Eval viewer reminder is duplicated as a workaround, not a fix
The `generate_review.py` reminder appears in the main eval section (Step 4) and again verbatim with all-caps
emphasis in the Cowork section with an explicit apology: *"Sorry in advance but I'm gonna go all caps here."*
Duplication with an apology is a strong signal that the instruction isn't landing reliably — the root cause
(eval viewer step getting skipped) should be addressed structurally, not patched with repetition.

### 5. No quick-start summary for experienced users
A model that has used this skill before must re-read all 486 lines to find a specific phase (e.g., the description
optimisation loop). There is no TL;DR, no section jump-table, and no "returning user" shortcut. The only navigation
aid is the core loop summary at the very end.

### 6. `references/schemas.md` scope is unclear in the body
The body says *"See `references/schemas.md` for the full schema"* once, for the `evals.json` `assertions` field.
The file presumably covers `grading.json`, `benchmark.json`, and the `eval_metadata.json` schemas too, but the
body never says this — the model may load it once for one schema and not realise it contains all the others.

### 7. Workspace layout is described but never illustrated
The body specifies *"Put results in `<skill-name>-workspace/` as a sibling to the skill directory"* and describes
the `iteration-N/eval-N/` directory tree, but never shows the structure visually. A small directory tree diagram
(like those in `AGENTS.md`) would make this unambiguous.

### 8. `run_loop.py` dependency on `claude` CLI is under-flagged
The description optimisation section depends on `claude -p` being available in the shell. This is only mentioned
in the Claude.ai-specific section. In the main optimisation workflow, a reader who doesn't have the Claude CLI
installed will hit a silent failure with no recovery path.

---

## Steps to Improve

1. **Extract "Description Optimization" (Steps 1–4) to `references/description-optimization.md`** and replace the
   body section with a 5-line pointer. This alone would bring the body under ~410 lines and solve the
   self-violation of the 500-line rule.

2. **Extract environment-specific instructions to `references/environment-notes.md`** — one section each for
   Claude Code, Claude.ai, and Cowork. Keep only a one-paragraph summary in the body pointing to the file.

3. **Fix the all-caps eval viewer instruction** — instead of a repeated all-caps reminder, restructure the
   eval-section steps so the viewer generation is step **3** (not step 4) and label it explicitly as a
   *blocking step* before any self-evaluation:
   > *"Generate the viewer before reading any outputs yourself — the human's review is the signal you need,
   > and seeing their reaction uncontaminated by your own assessment produces better iterations."*

4. **Strengthen the description with casual trigger phrases:**
   > *"… Also trigger when the user says 'improve this skill', 'this skill keeps missing', 'turn this into a
   > skill', 'my skill isn't triggering', or shows a workflow they want to automate — even without using the
   > word 'skill'."*

5. **Add a section jump-table** near the top for returning users:

   | What you want to do | Jump to |
   |---|---|
   | Write a new skill from scratch | Capture Intent → Interview → Write SKILL.md |
   | Improve an existing skill | Running and evaluating → Improving the skill |
   | Optimise description triggering | Description Optimization |
   | Package and share | Package and Present |

6. **Expand the `references/schemas.md` pointer** in the body to list all schemas it covers:
   > *"See `references/schemas.md` for schemas: `evals.json`, `eval_metadata.json`, `grading.json`,
   > `benchmark.json`, and `timing.json`."*

7. **Add a workspace directory tree** to the "Running and evaluating" section:
   ```
   <skill-name>-workspace/
   ├── iteration-1/
   │   ├── eval-0-happy-path/
   │   │   ├── with_skill/outputs/
   │   │   ├── without_skill/outputs/
   │   │   ├── eval_metadata.json
   │   │   └── timing.json
   │   └── benchmark.json
   └── skill-snapshot/   ← only when improving an existing skill
   ```

8. **Add a `claude` CLI prerequisite note** to the main Description Optimization section, not just the Claude.ai
   section:
   > *"This step requires the `claude` CLI (`claude -p`). Run `claude --version` to confirm availability before
   > starting the optimisation loop."*

