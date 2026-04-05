# Orchestrator Agent — Pipeline Workflow

## Agents

| Agent | Responsibility |
|---|---|
| `jira-researcher` | Fetch Jira issue details, linked issues, Xray test steps |
| `test-planner` | Read project context, discover patterns, produce self-contained plan |
| `test-automation` | Implement test cases, validate, label Jira issue |
| `test-code-reviewer` | Review implemented code, return structured findings |

---

## Pipeline

```
┌─────────────────────────────────────────────────────────────┐
│                       USER REQUEST                          │
│          ticket key provided?  OR  "random / any"           │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 1 · Research                                         │
│  Agent: jira-researcher                                     │
│  • key provided → pass directly                             │
│  • random/any   → query "Tests to automate" filter          │
│                                                             │
│  ⏳ Wait for structured Jira/Xray summary                   │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 2 · Plan                                             │
│  Agent: test-planner  (receives full Jira/Xray summary)     │
│  • Read project skills & AGENTS.md files                    │
│  • Discover codebase patterns, embed in plan                │
│  • Output: self-contained implementation plan               │
│                                                             │
│  📌 Note Complexity: Simple | Medium | Complex              │
│  ▶ No user approval pause — proceed automatically           │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 3 · Implement                                        │
│  Agent: test-automation  (receives plan only)               │
│  • Implement all test methods exactly as planned            │
│  • Validate for compilation errors                          │
│  • Add "automated" label to the Jira issue                  │
│                                                             │
│  ⏳ Wait for implementation confirmation                    │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
╔═════════════════════════════════════════════════════════════╗
║  PHASE 4 · Review & Fix Loop                                ║
║                                                             ║
║  Max iterations:  Simple → 1   |   Medium / Complex → 2    ║
║                                                             ║
║  Each iteration:                                            ║
║    1. test-code-reviewer reviews modified files             ║
║    2. No issues found  → exit loop ────────────────────►   ║
║    3. Issues found     → ask user to confirm fixes          ║
║    4. test-automation applies fixes                         ║
║    5. Repeat if iterations remain, otherwise collect        ║
║       unresolved issues and proceed                         ║
║                                                             ║
║  ⚠ Iteration 2 scope: verify iter-1 issues only            ║
╚═════════════════════════════════════════════════════════════╝
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│  PHASE 5 · Final Report  →  User                            │
│  • What was implemented (files, @DisplayName test methods)  │
│  • Jira "automated" label added: ✅ / ❌                    │
│  • Complexity + iterations used (N / max)                   │
│  • Unresolved review findings (if any)                      │
└─────────────────────────────────────────────────────────────┘
```

---

## Key Rules

1. Orchestrator **never** writes code or modifies files
2. Full Jira/Xray context flows **only** to `test-planner`
3. `test-automation` receives the **plan only** — it is self-contained
4. `test-code-reviewer` always receives the **specific changed files**
5. Review findings are passed **verbatim** back to `test-automation` for fixes
6. Iter-2 review is **scoped** — only re-checks iter-1 flagged issues
7. All 5 phases execute **in order** — no skipping

