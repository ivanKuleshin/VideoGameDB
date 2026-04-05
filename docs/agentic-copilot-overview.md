# Agentic Programming with GitHub Copilot: Limitations, Pitfalls & Best Practices

---

## What Is the Approach?

Agentic programming with GitHub Copilot goes beyond simple code completion. You define a system of **custom instructions**, **skills**, and **custom agents** that work together — often orchestrated through a pipeline — to perform multi-step tasks autonomously: research → plan → implement → review.

---

## Limitations of the Agentic Approach

| Limitation | Why It Matters |
|---|---|
| **Context window is finite** | Agents can only "see" so much at once. Large codebases, long plans, or verbose instructions compete for the same limited context. Critical details can get pushed out silently. |
| **No persistent memory across sessions** | Each agent invocation starts fresh. There is no built-in learning — the agent won't remember decisions from yesterday's session or from a previous pipeline run. |
| **Non-deterministic output** | The same prompt can produce different results on different runs. This makes pipelines harder to stabilize and debug than traditional CI scripts. |
| **Cascading errors in multi-agent pipelines** | A subtle mistake in an early phase (e.g., wrong Jira context, missed requirement) propagates through the entire chain. Downstream agents trust what they receive — they don't second-guess upstream output. |
| **No true reasoning or verification** | Agents simulate reasoning through pattern matching. They cannot *prove* correctness, run tests themselves without tool access, or guarantee logical consistency of generated code. |
| **Tool availability is session-dependent** | Agents can only use tools exposed to them. If a tool fails silently or an MCP server is unreachable, the pipeline may produce incomplete results without a clear error. |
| **Orchestration overhead** | Multi-agent coordination (orchestrator → researcher → planner → implementer → reviewer) adds complexity. The orchestrator itself is an LLM — it can misroute tasks, skip phases, or lose context between handoffs. |

---

## Common Pitfalls

### 1. Instruction Overload
Writing extremely detailed instructions trying to cover every edge case. The agent's context window fills up with rules, leaving less room for actual code and reasoning.

### 2. Conflicting Rules Across Layers
Instructions live in multiple places: `copilot-instructions.md`, `AGENTS.md`, skill files, agent prompts. When they conflict, the agent picks one silently — and it may not be the one you expected.

### 3. Over-Trusting Agent Output
Agents produce confident-looking code that compiles but may have subtle logic errors, missed edge cases, or patterns that *almost* match your conventions. Human review remains essential.

### 4. Brittle Pipelines
A tightly sequenced pipeline (Phase 1 → 2 → 3 → 4 → 5) assumes every phase succeeds perfectly. One weak link — a Jira API timeout, a vague test step, a bad plan — breaks the whole flow.

### 5. Agent Role Blurring
Without strict boundaries, agents drift outside their role — a "planner" starts writing code, a "reviewer" starts implementing fixes. Clear role separation needs constant reinforcement in prompts.

### 6. Phantom Context
Agents sometimes "hallucinate" file contents, method signatures, or project patterns they haven't actually read. Skipping explicit file reads in favor of assumptions leads to code that doesn't match reality.

### 7. Iteration Loop Without Exit
Review-fix loops (reviewer finds issues → implementer fixes → reviewer re-checks) can cycle without converging if the review criteria are ambiguous or the implementer disagrees. A hard iteration cap is essential.

---

## Best Practices

### Custom Instructions (`copilot-instructions.md`)

| Practice | Details |
|---|---|
| **Keep instructions concise and prioritized** | Put the most critical rules first. Agents weigh earlier content more heavily. Aim for under 50 rules. |
| **Use concrete examples over abstract rules** | `❌ "Follow good naming"` → `✅ "Methods: camelCase with verb prefixes (prepare*, create*, validate*)"` |
| **Avoid duplication across files** | Define a rule in one place, reference it from others. Contradictions between `copilot-instructions.md` and `AGENTS.md` confuse agents. |
| **Separate concerns clearly** | Coding style in instructions, architecture in `AGENTS.md`, test patterns in skills. Don't mix concerns in a single file. |
| **Include anti-patterns explicitly** | Telling agents what NOT to do is as important as telling them what to do. |

### Skills

| Practice | Details |
|---|---|
| **One skill = one domain** | `component-testing`, `db-testing`, `code-review` — not a single monolithic "testing" skill. Focused skills trigger more reliably. |
| **Write trigger-friendly descriptions** | The skill description determines when it activates. Include synonyms and casual phrases users might say. |
| **Include a quick example in every skill** | A 10-line code snippet teaches more than 50 lines of rules. Agents mimic patterns from examples. |
| **Reference, don't inline** | Keep large reference material (checklists, code patterns) in separate files and link from the skill. This saves context window space. |
| **Version-lock external skills** | Third-party skills change. Pin them (e.g., via `skills-lock.json`) to avoid unexpected behavior shifts. |

### Custom Agents

| Practice | Details |
|---|---|
| **Single Responsibility per agent** | One agent = one job. A researcher doesn't plan. A planner doesn't code. A reviewer doesn't fix. |
| **Restrict tool access to what's needed** | A planner needs `read_file` and `grep_search`, not `create_file` or `run_in_terminal`. Fewer tools = fewer mistakes. |
| **Choose the right model per agent** | Lightweight tasks (Jira fetching) → smaller/cheaper model. Complex tasks (planning, implementation) → most capable model. |
| **Design self-contained handoffs** | Each agent's output should contain everything the next agent needs. No implicit "go read the Jira ticket yourself" — that duplicates work and risks inconsistency. |
| **Cap iteration loops explicitly** | Define max review-fix iterations upfront based on task complexity. Prevent infinite loops. |
| **Make the orchestrator stateless** | The orchestrator coordinates — it doesn't hold business logic. If it fails, any agent's output should still be usable standalone. |
| **Test agents individually before orchestrating** | Run each agent in isolation to validate its behavior. Only compose the pipeline after each piece works reliably. |

---

## Key Takeaway

> Agentic programming amplifies your productivity — but it also amplifies your mistakes.
> The quality of AI-generated output is directly proportional to the quality of your instructions, skill definitions, and agent boundaries.
> Invest time in **clear structure**, **minimal duplication**, and **explicit constraints** — this is the real engineering in agentic workflows.

