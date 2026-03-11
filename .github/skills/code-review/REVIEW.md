# Skill Review: `code-review`

## Overview

A comprehensive code review agent for Java Spring Boot applications. At **224 lines**, it stays within the 500-line
limit. Has a `references/` directory for extended content, though it is not referenced from the body.

---

## Pros

- **Rich checklist coverage** — spans Code Quality, Java/Spring Boot Standards, Test Quality, DB & Persistence,
  API & REST, and Security. Leaves little room for missed categories.
- **Severity classification** — findings are labelled Critical / High / Medium / Low / Info, making prioritisation
  easy.
- **Concrete output template** — provides an exact Markdown structure the model should produce, including code
  snippet slots.
- **Project-aware standards** — the final section cross-references `component-testing`, `spring-boot-engineer`, and
  `db-testing` skills plus `copilot-instructions.md`, anchoring reviews to this project's actual rules.
- **Best-practices section** — reminds the reviewer to be constructive, specific, and to explain the *why* behind
  every finding.

---

## Cons

### 1. Duplicate "When to Use" section in the body
The skill-creator rule states: *"All 'when to use' info goes in the description, not in the body."*  
`## When to Use This Skill` is repeated in the SKILL.md body, adding noise and making the description feel
redundant.

### 2. Description is not "pushy" enough
The description lists valid scenarios but doesn't actively compel triggering. Per skill-creator guidance,
descriptions should be *"a little bit 'pushy'"* — especially for undertriggering skills. There is no phrase like
*"even if the user doesn't explicitly say 'review'"*.

### 3. Contradictory output format instructions
Two output formats are defined:
- *"When called from the orchestrator pipeline: use the compact table format from `code-reviewer.agent.md`"*
- *"Provide feedback as a Markdown document using this structure: …"*

The compact table format references a file (`code-reviewer.agent.md`) that is not present in `references/`, making
the instruction unresolvable.

### 4. `references/` directory is unused
A `references/` directory exists but no file inside it is referenced from SKILL.md. The checklist (≈60 lines)
would be a perfect candidate for extraction, improving the body's signal-to-noise ratio.

### 5. Heavy use of imperative bullet lists without rationale
The checklist uses `- [ ]` items with no explanation of *why* a rule matters. Per skill-creator principles, prefer
explaining the reasoning so the model can generalise, rather than mechanical checkbox compliance.

### 6. Generic "Best Practices for Reviewers" section adds length without value
Seven items like *"Be Constructive"* and *"Be Specific"* describe universal review etiquette, not
project-specific rules. They dilute the skill and could be removed entirely.

---

## Steps to Improve

1. **Move `## When to Use This Skill` into the description frontmatter** and remove the body section. Rewrite the
   description to be explicitly pushy:
   > *"… Trigger even when the user says things like 'take a look at this', 'does this look right', or 'any issues
   > here?' without mentioning code review explicitly."*

2. **Resolve the dual output-format contradiction.** Either:
   - Add `references/code-reviewer-agent.md` with the compact table template and reference it clearly, or
   - Remove the orchestrator-specific instruction if no orchestrator pipeline currently exists.

3. **Extract the full checklist to `references/checklist.md`** and reference it from SKILL.md with a one-line
   pointer: *"For the full review checklist, see `references/checklist.md`."* Keep only a short summary of
   categories in the body.

4. **Add rationale to key checklist items.** For example, instead of `- [ ] Constructor injection used`, write:
   *"Prefer constructor injection — it makes dependencies explicit, supports immutability, and simplifies testing."*

5. **Remove the "Best Practices for Reviewers" section** or condense it to one or two project-specific reminders
   (e.g., referencing `@TmsLink` for traceability) that aren't obvious from general engineering knowledge.

6. **Populate `references/`** with at least one file (e.g., good-vs-bad code examples) and reference it from the
   body so the progressive-disclosure architecture is actually used.

